package com.example.booklibrary.book.controller;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/paged")
@RequiredArgsConstructor
public class BookPagedController {

    private final BookService bookService;

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

