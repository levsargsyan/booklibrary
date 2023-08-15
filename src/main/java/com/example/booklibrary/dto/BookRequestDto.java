package com.example.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookRequestDto implements Serializable {

    @Null
    private Long id;

    @Null
    private int version;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String genre;

    @NotBlank
    private String description;

    @NotBlank
    private String isbn;

    @Pattern(regexp = "^(http|https)://[^\\s]+$", message = "url must be valid")
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate published;

    @NotBlank
    private String publisher;

}
