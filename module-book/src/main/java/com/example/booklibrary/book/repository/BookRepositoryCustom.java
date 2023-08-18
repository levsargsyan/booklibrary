package com.example.booklibrary.book.repository;

import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {
    Page<Book> searchBooks(BookSearchCommand searchCommand, Pageable pageable);
}
