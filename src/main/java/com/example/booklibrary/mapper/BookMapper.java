package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.BookDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    BookDto bookToBookDto(Book book);

    Book bookDtoToBook(BookDto bookDto);

    BookResponseDto bookToBookResponseDto(Book book);

    List<BookResponseDto> booksToBookResponseDtos(List<Book> books);

}
