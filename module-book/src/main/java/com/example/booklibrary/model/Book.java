package com.example.booklibrary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @NotBlank
    @Size(max = 255)
    @Column(name = "genre", nullable = false, length = 255)
    private String genre;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @NotBlank
    @Size(min = 13, max = 13)
    @Column(name = "isbn", unique = true, nullable = false, length = 13)
    private String isbn;

    @NotBlank
    @Pattern(regexp = "^(http|https)://[^\\s]+$")
    @Size(max = 255)
    @Column(name = "image", nullable = false, length = 255)
    private String image;

    @NotNull
    @Column(name = "published", nullable = false)
    private LocalDate published;

    @NotBlank
    @Size(max = 255)
    @Column(name = "publisher", nullable = false, length = 255)
    private String publisher;
}
