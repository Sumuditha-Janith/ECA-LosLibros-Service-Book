package lk.ijse.eca.bookservice.service.impl;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.entity.Book;
import lk.ijse.eca.bookservice.mapper.BookMapper;
import lk.ijse.eca.bookservice.exception.DuplicateBookException;
import lk.ijse.eca.bookservice.exception.FileOperationException;
import lk.ijse.eca.bookservice.exception.BookNotFoundException;
import lk.ijse.eca.bookservice.repository.BookRepository;
import lk.ijse.eca.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Value("${app.storage.path}")
    private String storagePathStr;

    private Path storagePath;

    @Override
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {
        log.debug("Creating book with ISBN: {}", dto.getIsbn());

        if (bookRepository.existsById(dto.getIsbn())) {
            log.warn("Duplicate ISBN detected: {}", dto.getIsbn());
            throw new DuplicateBookException(dto.getIsbn());
        }

        String coverId = UUID.randomUUID().toString();

        Book book = bookMapper.toEntity(dto);
        book.setCoverImage(coverId);

        bookRepository.save(book);
        log.debug("Book persisted to DB: {}", dto.getIsbn());

        saveCover(coverId, dto.getCoverImage());

        log.info("Book created successfully: {}", dto.getIsbn());
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional
    public BookResponseDTO updateBook(String isbn, BookRequestDTO dto) {
        log.debug("Updating book with ISBN: {}", isbn);

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> {
                    log.warn("Book not found for update: {}", isbn);
                    return new BookNotFoundException(isbn);
                });

        String oldCoverId = book.getCoverImage();
        boolean coverChanged = dto.getCoverImage() != null && !dto.getCoverImage().isEmpty();
        String newCoverId = coverChanged ? UUID.randomUUID().toString() : oldCoverId;

        bookMapper.updateEntity(dto, book);
        book.setCoverImage(newCoverId);

        bookRepository.save(book);
        log.debug("Book updated in DB: {}", isbn);

        if (coverChanged) {
            saveCover(newCoverId, dto.getCoverImage());
            tryDeleteCover(oldCoverId);
        }

        log.info("Book updated successfully: {}", isbn);
        return bookMapper.toResponseDto(book);
    }

    @Override
    @Transactional
    public void deleteBook(String isbn) {
        log.debug("Deleting book with ISBN: {}", isbn);

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> {
                    log.warn("Book not found for deletion: {}", isbn);
                    return new BookNotFoundException(isbn);
                });

        String coverId = book.getCoverImage();

        bookRepository.delete(book);
        log.debug("Book marked for deletion in DB: {}", isbn);

        deleteCover(coverId);

        log.info("Book deleted successfully: {}", isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDTO getBook(String isbn) {
        log.debug("Fetching book with ISBN: {}", isbn);
        return bookRepository.findById(isbn)
                .map(bookMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("Book not found: {}", isbn);
                    return new BookNotFoundException(isbn);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        log.debug("Fetching all books");
        List<BookResponseDTO> books = bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} books", books.size());
        return books;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getBookCover(String isbn) {
        log.debug("Fetching cover for book ISBN: {}", isbn);
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> {
                    log.warn("Book not found: {}", isbn);
                    return new BookNotFoundException(isbn);
                });
        Path filePath = storagePath().resolve(book.getCoverImage());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read cover for book: {}", isbn, e);
            throw new FileOperationException("Failed to read cover for book: " + isbn, e);
        }
    }

    private Path storagePath() {
        if (storagePath == null) {
            storagePath = Paths.get(storagePathStr);
        }
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

    private void saveCover(String coverId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Cover image must not be empty");
        }
        Path filePath = storagePath().resolve(coverId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Cover saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save cover: {}", filePath, e);
            throw new FileOperationException("Failed to save cover file: " + coverId, e);
        }
    }

    private void deleteCover(String coverId) {
        Path filePath = storagePath().resolve(coverId);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Cover deleted: {}", filePath);
            } else {
                log.warn("Cover file not found on disk (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete cover: {}", filePath, e);
            throw new FileOperationException("Failed to delete cover file: " + coverId, e);
        }
    }

    private void tryDeleteCover(String coverId) {
        try {
            deleteCover(coverId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old cover file '{}'. Manual cleanup may be required.", coverId);
        }
    }
}
