package com.example.booklibrary.controller;

import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.dto.search.BookSearchCommand;
import com.example.booklibrary.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/paged")
@AllArgsConstructor
public class BookPagedController {

    private BookService bookService;

    @GetMapping
    public PagedModel<EntityModel<BookResponseDto>> getAllBooks(Pageable pageable) {
        Page<BookResponseDto> booksPaginatedDto = bookService.getBooksPaginated(pageable);
        return bookService.assemblePagedModel(pageable, booksPaginatedDto, "/api/v1/books/paged");
    }

    @PostMapping("/search")
    public PagedModel<EntityModel<BookResponseDto>> searchBooks(@RequestBody BookSearchCommand searchCommand, Pageable pageable) {
        Page<BookResponseDto> booksPaginatedDto = bookService.searchBooks(searchCommand, pageable);
        return bookService.assemblePagedModel(pageable, booksPaginatedDto, "/api/v1/books/paged/search");
    }
}

