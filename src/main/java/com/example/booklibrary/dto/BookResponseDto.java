package com.example.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookResponseDto {
    private Long id;

    private String title;

    private String author;

    private String genre;

    private String description;

    private String isbn;

    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate published;

    private String publisher;
}
