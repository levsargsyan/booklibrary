package com.example.booklibrary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class BookRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -2397469302679684797L;

    @Null
    private Long id;

    @Null
    private int version;

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
    @Size(min = 13, max = 13)
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

}
