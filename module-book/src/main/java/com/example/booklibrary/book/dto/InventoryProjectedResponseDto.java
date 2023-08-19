package com.example.booklibrary.book.dto;

public record InventoryProjectedResponseDto(
        Long id,
        Integer version,
        Integer count,
        Long bookId,
        String bookIsbn
) {}
