package com.example.booklibrary.service;

import com.example.booklibrary.dto.BookRequestDto;
import com.example.booklibrary.dto.BookResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

public interface BookService {

    List<BookResponseDto> getAllBooks();

    BookResponseDto getBook(Long id);

    BookResponseDto saveBook(BookRequestDto bookRequestDto);

    void deleteBook(Long id);

    PagedModel<EntityModel<BookResponseDto>> assemblePagedModel(Pageable pageable, String path);
}
