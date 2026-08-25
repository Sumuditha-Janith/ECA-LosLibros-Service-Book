package lk.ijse.eca.bookservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookController {

    private final BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDTO> createBook(
            @Validated({Default.class, BookRequestDTO.OnCreate.class}) @ModelAttribute BookRequestDTO dto) {
        log.info("POST /api/v1/books - ISBN: {}", dto.getIsbn());
        BookResponseDTO response = bookService.createBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{isbn}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponseDTO> updateBook(
            @PathVariable String isbn,
            @Valid @ModelAttribute BookRequestDTO dto) {
        log.info("PUT /api/v1/books/{}", isbn);
        BookResponseDTO response = bookService.updateBook(isbn, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        log.info("DELETE /api/v1/books/{}", isbn);
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponseDTO> getBook(@PathVariable String isbn) {
        log.info("GET /api/v1/books/{}", isbn);
        BookResponseDTO response = bookService.getBook(isbn);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        log.info("GET /api/v1/books");
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{isbn}/cover")
    public ResponseEntity<byte[]> getBookCover(@PathVariable String isbn) {
        log.info("GET /api/v1/books/{}/cover", isbn);
        byte[] cover = bookService.getBookCover(isbn);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(cover);
    }
}
