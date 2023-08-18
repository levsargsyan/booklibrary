package com.example.booklibrary.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6711051233666925635L;

    private Long id;

    private Integer version;

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
