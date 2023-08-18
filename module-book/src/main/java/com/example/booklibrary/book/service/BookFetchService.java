package com.example.booklibrary.book.service;

import com.example.booklibrary.book.model.Book;

import java.util.List;

public interface BookFetchService {

    List<Book> fetchBooks();
}
