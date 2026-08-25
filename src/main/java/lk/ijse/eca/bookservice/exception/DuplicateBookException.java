package lk.ijse.eca.bookservice.exception;

public class DuplicateBookException extends RuntimeException {

    public DuplicateBookException(String isbn) {
        super("Book with ISBN '" + isbn + "' already exists");
    }
}
