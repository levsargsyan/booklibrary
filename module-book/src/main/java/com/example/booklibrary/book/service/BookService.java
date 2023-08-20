package com.example.booklibrary.book.service;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryProjectedResponseDto;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.Set;

public interface BookService {

    List<BookWithInventoryResponseDto> getAllBooks();

    List<InventoryProjectedResponseDto> getAllInventories();

    BookResponseDto getBook(Long id);

    BookWithInventoryResponseDto getBookWithInventory(Long id);

    Long getAllBooksCount();

    BookWithInventoryResponseDto saveBook(BookWithInventoryRequestDto bookWithInventoryRequestDto);

    BookWithInventoryResponseDto updateBook(Long id, BookWithInventoryRequestDto bookWithInventoryRequestDto);

    void deleteBook(Long id);

    Page<BookResponseDto> searchBooks(BookSearchCommand searchCommand, Pageable pageable);

    Page<BookResponseDto> getBooksPaginated(Pageable pageable);

    List<BookWithInventoryResponseDto> getBooksByAuthorAndGenre(Set<String> authors, Set<String> genres);

    List<BookWithInventoryResponseDto> getAvailableBooksByAuthors(Set<String> authors, Integer count);

    List<BookWithInventoryResponseDto> getAvailableBooksByGenres(Set<String> genres, Integer count);

    String getBookAuthor(Long bookId);

    String getBookGenre(Long bookId);

    PagedModel<EntityModel<BookResponseDto>> assemblePagedModel(
            Pageable pageable,
            Page<BookResponseDto> booksPaginatedDto,
            String path);

    void checkData(BookWithInventoryRequestDto requestDto, BookWithInventoryResponseDto existingDto);

    void decrementBookCount(Long inventoryId, Integer count);

    List<BookWithInventoryResponseDto> getAvailableLastAddedBooks(Integer i);
}
