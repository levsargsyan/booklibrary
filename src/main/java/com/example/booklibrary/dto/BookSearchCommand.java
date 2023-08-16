package com.example.booklibrary.dto;

import com.example.booklibrary.constant.SearchOperation;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookSearchCommand {
    private String title;
    private String author;
    private String genre;
    private String description;
    private String isbn;
    private String image;
    private String publisher;
    private LocalDate published;
    private LocalDate publishedFrom;
    private LocalDate publishedTo;
    private SearchOperation operation = SearchOperation.AND;  // Default to AND
}
