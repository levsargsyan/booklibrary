package com.example.booklibrary.controller;

import com.example.booklibrary.dto.BookDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/view/books")
@AllArgsConstructor
public class BookViewController {

    private BookService bookService;

    @GetMapping("/paged")
    public PagedModel<EntityModel<BookResponseDto>> getAllBooks(Pageable pageable) {
        Page<BookResponseDto> booksPaginatedDto = bookService.getBooksPaginated(pageable);
        PagedModel<EntityModel<BookResponseDto>> pagedModel = bookService.assemblePagedModel(booksPaginatedDto);
        addPaginationLinks(pagedModel, booksPaginatedDto, pageable);
        return pagedModel;
    }

    private void addPaginationLinks(PagedModel<EntityModel<BookResponseDto>> pagedModel, Page<BookResponseDto> books, Pageable pageable) {
        UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromPath("/api/v1/view/books/paged");

        pagedModel.add(buildLink(baseBuilder, pageable, "self"));

        if (books.hasNext()) {
            Pageable nextPage = pageable.next();
            pagedModel.add(buildLink(baseBuilder, nextPage, "next"));

            int lastIndex = books.getTotalPages() - 1;
            pagedModel.add(buildLink(baseBuilder, PageRequest.of(lastIndex, pageable.getPageSize()), "last"));
        }
        if (books.hasPrevious()) {
            Pageable previousPage = pageable.previousOrFirst();
            pagedModel.add(buildLink(baseBuilder, previousPage, "prev"));
            pagedModel.add(buildLink(baseBuilder, PageRequest.of(0, pageable.getPageSize()), "first"));
        }
    }

    private Link buildLink(UriComponentsBuilder baseBuilder, Pageable pageable, String rel) {
        UriComponents uriComponents = baseBuilder.cloneBuilder()
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .build();

        return Link.of(uriComponents.toUriString(), rel);
    }
}

