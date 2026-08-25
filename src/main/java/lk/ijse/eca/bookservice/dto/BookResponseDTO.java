package lk.ijse.eca.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class BookResponseDTO {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String publishedYear;
    private String genre;
    private String coverImageUrl;
}
