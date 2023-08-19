package com.example.booklibrary.book.mapper;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.model.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book bookWithInventoryRequestDtoToBook(BookWithInventoryRequestDto bookWithInventoryRequestDto);

    BookResponseDto bookToBookResponseDto(Book book);

    BookWithInventoryResponseDto bookToBookWithInventoryResponseDto(Book book);

    List<BookWithInventoryResponseDto> booksToBookWithInventoryResponseDtos(List<Book> books);


    @AfterMapping
    default void setBookToInventory(@MappingTarget Book book) {
        if (book.getInventory() != null) {
            book.getInventory().setBook(book);
        }
    }
}
