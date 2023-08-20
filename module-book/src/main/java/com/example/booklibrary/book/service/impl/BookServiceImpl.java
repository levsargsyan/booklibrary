package com.example.booklibrary.book.service.impl;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryProjectedResponseDto;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.mapper.BookMapper;
import com.example.booklibrary.book.model.Book;
import com.example.booklibrary.book.model.Inventory;
import com.example.booklibrary.book.repository.BookRepository;
import com.example.booklibrary.book.repository.InventoryRepository;
import com.example.booklibrary.book.service.BookService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "book", key = "#id")
    @Override
    public BookResponseDto getBook(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookResponseDto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public BookWithInventoryResponseDto getBookWithInventory(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::bookToBookWithInventoryResponseDto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book with inventory not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getAllBooks() {
        return bookMapper.booksToBookWithInventoryResponseDtos(bookRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public Long getAllBooksCount() {
        return bookRepository.count();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "pagedBooks", allEntries = true),
            @CacheEvict(value = "searchedBooks", allEntries = true)
    })
    @Override
    public BookWithInventoryResponseDto saveBook(BookWithInventoryRequestDto bookWithInventoryRequestDto) {
        Book book = bookRepository.save(bookMapper.bookWithInventoryRequestDtoToBook(bookWithInventoryRequestDto));
        return bookMapper.bookToBookWithInventoryResponseDto(book);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
            @CacheEvict(value = "pagedBooks", allEntries = true),
            @CacheEvict(value = "searchedBooks", allEntries = true)
    })
    @Override
    public BookWithInventoryResponseDto updateBook(Long id, BookWithInventoryRequestDto updatedBookWithInventoryRequestDto) {
        BookWithInventoryResponseDto existingBookDto = getBookWithInventory(id);
        checkData(updatedBookWithInventoryRequestDto, existingBookDto);
        updatedBookWithInventoryRequestDto.setId(existingBookDto.getId());
        updatedBookWithInventoryRequestDto.setVersion(existingBookDto.getVersion());
        updatedBookWithInventoryRequestDto.getInventory().setId(existingBookDto.getInventory().getId());
        updatedBookWithInventoryRequestDto.getInventory().setVersion(existingBookDto.getInventory().getVersion());
        return saveBook(updatedBookWithInventoryRequestDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "book", key = "#id"),
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

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getAvailableLastAddedBooks(Integer count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Book> books = bookRepository.findByOrderByIdDesc(pageRequest);
        return books.stream()
                .map(bookMapper::bookToBookWithInventoryResponseDto)
                .filter(book -> book.getInventory().getCount() >= 1)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getAvailableBooksByAuthors(Set<String> authors, Integer count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Book> books = bookRepository.findByAuthorInOrderByPublishedDesc(authors, pageRequest);
        return books.stream()
                .map(bookMapper::bookToBookWithInventoryResponseDto)
                .filter(book -> book.getInventory().getCount() >= 1)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getAvailableBooksByGenres(Set<String> genres, Integer count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Book> books = bookRepository.findByGenreInOrderByPublishedDesc(genres, pageRequest);
        return books.stream()
                .map(bookMapper::bookToBookWithInventoryResponseDto)
                .filter(book -> book.getInventory().getCount() >= 1)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public String getBookAuthor(Long bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getAuthor)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found with ID: " + bookId));
    }

    @Transactional(readOnly = true)
    @Override
    public String getBookGenre(Long bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getGenre)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found with ID: " + bookId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getBooksByAuthorAndGenre(Set<String> authors, Set<String> genres) {
        List<Book> books = bookRepository.findByAuthorInAndGenreIn(new ArrayList<>(authors), new ArrayList<>(genres));
        return bookMapper.booksToBookWithInventoryResponseDtos(books);
    }

    public List<InventoryProjectedResponseDto> getAllInventories() {
        return inventoryRepository.findAllProjectedBy();
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

    @Transactional
    @Override
    public void checkData(BookWithInventoryRequestDto requestDto, BookWithInventoryResponseDto existingDto) {

        if (existingDto == null) {
            if (bookRepository.existsByIsbn(requestDto.getIsbn())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Isbn already in use.");
            }
        } else {
            boolean isIsbnDifferentFromExisting = !requestDto.getIsbn()
                    .equals(existingDto.getIsbn());

            if (isIsbnDifferentFromExisting && bookRepository.existsByIsbn(requestDto.getIsbn())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Isbn already in use.");
            }
        }
    }

    @Transactional
    @Override
    public void decrementBookCount(Long inventoryId, Integer count) {
        try {
            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND,
                            "Inventory not found for ID: " + inventoryId));

            if (inventory.getCount() - count < 0) {
                throw new ResponseStatusException(
                        BAD_REQUEST,
                        String.format("You requested %d books, but only %d are in stock.", count, inventory.getCount()));
            }

            inventory.setCount(inventory.getCount() - count);
        } catch (OptimisticLockException e) {
            throw new ResponseStatusException(
                    CONFLICT,
                    "The book inventory was updated by another user. Please try again.");
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

