package com.example.booklibrary.book.helper;

import com.example.booklibrary.book.dto.*;
import com.example.booklibrary.book.model.Book;
import com.example.booklibrary.book.model.Inventory;

import java.time.LocalDate;

public class TestHelper {

    public static BookWithInventoryRequestDto createWithInventoryRequestDto() {
        BookWithInventoryRequestDto requestDto = new BookWithInventoryRequestDto();

        requestDto.setTitle("Alice in Wonderland");
        requestDto.setAuthor("Lewis Carroll");
        requestDto.setGenre("Fantasy");
        requestDto.setDescription("A classic tale of a girl named Alice...");
        requestDto.setIsbn("9797822925048");
        requestDto.setImage("http://placeimg.com/480/640/any");
        requestDto.setPublished(LocalDate.of(2023, 8, 21));
        requestDto.setPublisher("Macmillan");

        InventoryRequestDto inventoryDto = new InventoryRequestDto();
        inventoryDto.setCount(15);

        requestDto.setInventory(inventoryDto);

        return requestDto;
    }

    public static BookWithInventoryResponseDto createWithInventoryResponseDto() {
        BookWithInventoryResponseDto responseDto = new BookWithInventoryResponseDto();

        responseDto.setTitle("Alice in Wonderland");
        responseDto.setAuthor("Lewis Carroll");
        responseDto.setGenre("Fantasy");
        responseDto.setDescription("A classic tale of a girl named Alice...");
        responseDto.setIsbn("9797822925048");
        responseDto.setImage("http://placeimg.com/480/640/any");
        responseDto.setPublished(LocalDate.of(2023, 8, 21));
        responseDto.setPublisher("Macmillan");

        InventoryResponseDto inventoryDto = new InventoryResponseDto();
        inventoryDto.setCount(15);

        responseDto.setInventory(inventoryDto);

        return responseDto;
    }

    public static InventoryProjectedResponseDto createInventoryProjectedResponseDto() {

        return new InventoryProjectedResponseDto(
                1L,
                0,
                10,
                4L,
                "1234567891234");
    }

    public static BookResponseDto createBookResponseDto() {
        BookResponseDto responseDto = new BookResponseDto();

        responseDto.setId(1L);
        responseDto.setVersion(0);
        responseDto.setTitle("Alice in Wonderland");
        responseDto.setAuthor("Lewis Carroll");
        responseDto.setGenre("Fantasy");
        responseDto.setDescription("A classic tale of a girl named Alice...");
        responseDto.setIsbn("9797822925048");
        responseDto.setImage("http://placeimg.com/480/640/any");
        responseDto.setPublished(LocalDate.of(2022, 8, 21));
        responseDto.setPublisher("Macmillan");

        return responseDto;
    }

    public static Book createBook() {
        Book book = new Book();

        book.setId(1L);
        book.setVersion(0);
        book.setTitle("Alice in Wonderland");
        book.setAuthor("Lewis Carroll");
        book.setGenre("Fantasy");
        book.setDescription("A classic tale of a girl named Alice...");
        book.setIsbn("9797822925048");
        book.setImage("http://placeimg.com/480/640/any");
        book.setPublished(LocalDate.of(2022, 8, 21));
        book.setPublisher("Macmillan");

        Inventory inventory = new Inventory();
        inventory.setCount(15);

        book.setInventory(inventory);

        return book;
    }
}
