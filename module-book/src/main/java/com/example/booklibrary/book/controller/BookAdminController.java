package com.example.booklibrary.book.controller;

import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api/v1/books")
@RequiredArgsConstructor
public class BookAdminController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookWithInventoryResponseDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookWithInventoryResponseDto> getBookById(@PathVariable Long id) {
        BookWithInventoryResponseDto bookWithInventoryResponseDto = bookService.getBookWithInventory(id);
        if (bookWithInventoryResponseDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(bookWithInventoryResponseDto);
    }

    @PostMapping
    public ResponseEntity<BookWithInventoryResponseDto> createBook(@Valid @RequestBody BookWithInventoryRequestDto bookRequestDto) {
        BookWithInventoryResponseDto savedBookDto = bookService.saveBook(bookRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookWithInventoryResponseDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookWithInventoryRequestDto bookRequestDto) {
        BookWithInventoryResponseDto updatedBookDto = bookService.updateBook(id, bookRequestDto);
        if (updatedBookDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedBookDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

