package com.example.booklibrary.book.mapper;

import com.example.booklibrary.book.dto.BookRequestDto;
import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book bookRequestDtoToBook(BookRequestDto bookRequestDto);

    BookResponseDto bookToBookResponseDto(Book book);

    List<BookResponseDto> booksToBookResponseDtos(List<Book> books);

}
