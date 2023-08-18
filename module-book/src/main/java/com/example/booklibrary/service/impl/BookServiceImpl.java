package com.example.booklibrary.service.impl;

import com.example.booklibrary.dto.BookRequestDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.dto.search.BookSearchCommand;
import com.example.booklibrary.mapper.BookMapper;
import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "allBooks")
    @Override
    public List<BookResponseDto> getAllBooks() {
        return bookMapper.booksToBookResponseDtos(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "book", key = "#id")
    @Override
    public BookResponseDto getBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        return bookMapper.bookToBookResponseDto(book);
    }

    @Transactional
    @CachePut(value = "book", key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = "allBooks", allEntries = true),
            @CacheEvict(value = "pagedBooks", allEntries = true),
            @CacheEvict(value = "searchedBooks", allEntries = true)
    })
    @Override
    public BookResponseDto saveBook(BookRequestDto bookRequestDto) {
        checkData(bookRequestDto);
        Book book = bookRepository.save(bookMapper.bookRequestDtoToBook(bookRequestDto));
        return bookMapper.bookToBookResponseDto(book);
    }

    @Transactional
    @CachePut(value = "book", key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = "allBooks", allEntries = true),
            @CacheEvict(value = "pagedBooks", allEntries = true),
            @CacheEvict(value = "searchedBooks", allEntries = true)
    })
    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto updatedBookRequestDto) {
        checkData(updatedBookRequestDto);
        BookResponseDto existingBookDto = getBook(id);
        if (Objects.nonNull(existingBookDto)) {
            updatedBookRequestDto.setId(existingBookDto.getId());
            updatedBookRequestDto.setVersion(existingBookDto.getVersion());
            return saveBook(updatedBookRequestDto);
        }

        return null;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "allBooks", allEntries = true),
            @CacheEvict(value = "pagedBooks", allEntries = true),
            @CacheEvict(value = "searchedBooks", allEntries = true)
    })
    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "searchedBooks", key = "#searchCommand.toString() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Override
    public Page<BookResponseDto> searchBooks(BookSearchCommand searchCommand, Pageable pageable) {
        return bookRepository.searchBooks(searchCommand, pageable).map(bookMapper::bookToBookResponseDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pagedBooks", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Override
    public Page<BookResponseDto> getBooksPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::bookToBookResponseDto);
    }

    @Override
    public PagedModel<EntityModel<BookResponseDto>> assemblePagedModel(
            Pageable pageable,
            Page<BookResponseDto> booksPaginatedDto,
            String path) {
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

    private void checkData(BookRequestDto bookRequestDto) {
        if (bookRepository.existsByIsbn(bookRequestDto.getIsbn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Isbn already in use.");
        }
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

