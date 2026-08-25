package lk.ijse.eca.bookservice.service;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;

import java.util.List;

public interface BookService {

    BookResponseDTO createBook(BookRequestDTO dto);

    BookResponseDTO updateBook(String isbn, BookRequestDTO dto);

    void deleteBook(String isbn);

    BookResponseDTO getBook(String isbn);

    List<BookResponseDTO> getAllBooks();

    byte[] getBookCover(String isbn);
}
