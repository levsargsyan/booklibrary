package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.BookRequestDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book bookRequestDtoToBook(BookRequestDto bookRequestDto);

    BookResponseDto bookToBookResponseDto(Book book);

    List<BookResponseDto> booksToBookResponseDtos(List<Book> books);

}
