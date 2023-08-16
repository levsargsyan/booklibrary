package com.example.booklibrary.service;

import com.example.booklibrary.model.Book;

import java.util.List;

public interface BookFetchService {

    List<Book> fetchBooks();
}
