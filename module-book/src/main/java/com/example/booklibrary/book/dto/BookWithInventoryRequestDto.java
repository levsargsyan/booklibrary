package com.example.booklibrary.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookWithInventoryRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -9186422389027848574L;

    @Null
    private Long id;

    @Null
    private Integer version;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String author;

    @NotBlank
    @Size(max = 255)
    private String genre;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotBlank
    @Size(min = 13, max = 13)
    private String isbn;

    @Pattern(regexp = "^(http|https)://[^\\s]+$", message = "url must be valid")
    @Size(max = 255)
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @PastOrPresent
    private LocalDate published;

    @NotBlank
    @Size(max = 255)
    private String publisher;

    @Valid
    @NotNull
    private InventoryRequestDto inventory;
}
