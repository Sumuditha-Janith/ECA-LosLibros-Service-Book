package lk.ijse.eca.bookservice.mapper;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.entity.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class BookMapper {

    @Mapping(target = "coverImageUrl", expression = "java(buildCoverUrl(book))")
    public abstract BookResponseDTO toResponseDto(Book book);

    @Mapping(target = "coverImage", ignore = true)
    public abstract Book toEntity(BookRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    public abstract void updateEntity(BookRequestDTO dto, @MappingTarget Book book);

    protected String buildCoverUrl(Book book) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/books/{isbn}/cover")
                .buildAndExpand(book.getIsbn())
                .toUriString();
    }
}
