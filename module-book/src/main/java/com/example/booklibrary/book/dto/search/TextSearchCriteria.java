package com.example.booklibrary.book.dto.search;

import com.example.booklibrary.book.constant.SearchFieldOperation;
import lombok.Data;

@Data
public class TextSearchCriteria {
    private String value;
    private SearchFieldOperation operation = SearchFieldOperation.EQUAL; // Default to EQUAL
}
