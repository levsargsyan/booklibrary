package com.example.booklibrary.service.impl;

import com.example.booklibrary.dto.BookRequestDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookMapper.booksToBookResponseDtos(bookRepository.findAll());
    }

    @Override
    public BookResponseDto getBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null); // or throw an exception if not found
        return bookMapper.bookToBookResponseDto(book);
    }

    @Override
    public BookResponseDto saveBook(BookRequestDto bookRequestDto) {
        Book book = bookRepository.save(bookMapper.bookRequestDtoToBook(bookRequestDto));
        return bookMapper.bookToBookResponseDto(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public PagedModel<EntityModel<BookResponseDto>> assemblePagedModel(Pageable pageable, String path) {
        Page<BookResponseDto> booksPaginatedDto = getBooksPaginated(pageable);

        List<EntityModel<BookResponseDto>> bookDtoResources = booksPaginatedDto.getContent()
                .stream()
                .map(EntityModel::of)
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                booksPaginatedDto.getSize(),
                booksPaginatedDto.getNumber(),
                booksPaginatedDto.getTotalElements(),
                booksPaginatedDto.getTotalPages());

        PagedModel<EntityModel<BookResponseDto>> pagedModel = PagedModel.of(bookDtoResources, metadata);

        addPaginationLinks(pagedModel, booksPaginatedDto, pageable, path);

        return pagedModel;
    }

    private Page<BookResponseDto> getBooksPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::bookToBookResponseDto);
    }

    private void addPaginationLinks(PagedModel<EntityModel<BookResponseDto>> pagedModel,
                                    Page<BookResponseDto> books,
                                    Pageable pageable,
                                    String path) {
        UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromPath(path);

        pagedModel.add(buildLink(baseBuilder, pageable, "self"));

        if (books.hasNext()) {
            Pageable nextPage = pageable.next();
            pagedModel.add(buildLink(baseBuilder, nextPage, "next"));
            pagedModel.add(buildLink(baseBuilder, PageRequest.of(books.getTotalPages() - 1, pageable.getPageSize()), "last"));
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

