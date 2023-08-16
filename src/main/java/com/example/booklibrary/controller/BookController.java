package com.example.booklibrary.controller;

import com.example.booklibrary.dto.BookRequestDto;
import com.example.booklibrary.dto.BookResponseDto;
import com.example.booklibrary.service.BookService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        BookResponseDto bookResponseDto = bookService.getBook(id);
        if (bookResponseDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookResponseDto);
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        BookResponseDto savedBookRequestDto = bookService.saveBook(bookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequestDto updatedBookRequestDto) {
        BookResponseDto bookResponseDto = bookService.updateBook(id, updatedBookRequestDto);
        if (bookResponseDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

