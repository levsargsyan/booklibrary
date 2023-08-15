package com.example.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookRequestDto implements Serializable {

    @JsonIgnore
    private Long id;

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

    @Pattern(regexp = "^(http|https)://[^\\s]+$")
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate published;

    @NotBlank
    private String publisher;

}
