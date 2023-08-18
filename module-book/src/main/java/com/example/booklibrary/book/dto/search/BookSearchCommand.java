package com.example.booklibrary.book.dto.search;

import com.example.booklibrary.book.constant.SearchOperation;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookSearchCommand {
    private TextSearchCriteria title;
    private TextSearchCriteria author;
    private TextSearchCriteria genre;
    private TextSearchCriteria description;
    private TextSearchCriteria isbn;
    private TextSearchCriteria image;
    private TextSearchCriteria publisher;
    private LocalDate published;
    private LocalDate publishedFrom;
    private LocalDate publishedTo;
    private SearchOperation operation = SearchOperation.AND;  // Default to AND
}
