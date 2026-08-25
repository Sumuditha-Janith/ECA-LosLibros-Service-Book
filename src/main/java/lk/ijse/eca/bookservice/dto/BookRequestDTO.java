package lk.ijse.eca.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.eca.bookservice.validation.ValidImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BookRequestDTO {

    public interface OnCreate {}

    @NotBlank(groups = OnCreate.class, message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String publisher;

    private String publishedYear;

    private String genre;

    @NotNull(groups = OnCreate.class, message = "Cover image is required")
    @ValidImage
    private MultipartFile coverImage;
}
