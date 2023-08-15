package com.example.booklibrary.controller;

import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books/paged")
@AllArgsConstructor
public class BookPagedController {

    private BookService bookService;

    @GetMapping
    public PagedModel<EntityModel<BookResponseDto>> getAllBooks(Pageable pageable) {
        return bookService.assemblePagedModel(pageable, "/api/v1/books/paged");
    }
}

