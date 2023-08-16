package com.example.booklibrary.repository;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.dto.BookSearchCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {
    Page<Book> searchBooks(BookSearchCommand searchCommand, Pageable pageable);
}
