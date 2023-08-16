package com.example.booklibrary.dto.search;

import com.example.booklibrary.constant.SearchFieldOperation;
import lombok.Data;

@Data
public class TextSearchCriteria {
    private String value;
    private SearchFieldOperation operation = SearchFieldOperation.EQUAL; // Default to EQUAL
}
