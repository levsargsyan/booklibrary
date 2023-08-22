package com.example.booklibrary.book.controller;

import com.example.booklibrary.book.dto.BookWithInventoryRequestDto;
import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryProjectedResponseDto;
import com.example.booklibrary.book.helper.TestHelper;
import com.example.booklibrary.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static com.example.booklibrary.book.helper.TestHelper.createWithInventoryResponseDto;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookAdminController.class)
class BookAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void testGetAllBooks() throws Exception {
        List<BookWithInventoryResponseDto> emptyList = Collections.emptyList();

        when(bookService.getAllBooks()).thenReturn(emptyList);

        mockMvc.perform(get("/manage/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() throws Exception {
        BookWithInventoryResponseDto responseDto = createWithInventoryResponseDto();
        Long bookId = 1L;

        when(bookService.getBookWithInventory(bookId)).thenReturn(responseDto);

        mockMvc.perform(get("/manage/api/v1/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(bookService, times(1)).getBookWithInventory(bookId);
    }

    @Test
    void testCreateBook() throws Exception {
        BookWithInventoryResponseDto responseDto = createWithInventoryResponseDto();

        when(bookService.saveBook(any(BookWithInventoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/manage/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestHelper.createWithInventoryRequestDto())))
                .andExpect(status().isCreated());

        verify(bookService, times(1)).saveBook(any(BookWithInventoryRequestDto.class));
    }

    @Test
    void testUpdateBook_BookFound() throws Exception {
        Long bookId = 1L;
        BookWithInventoryResponseDto responseDto = createWithInventoryResponseDto();

        when(bookService.updateBook(eq(bookId), any(BookWithInventoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/manage/api/v1/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestHelper.createWithInventoryRequestDto())))
                .andExpect(status().isOk());

        verify(bookService, times(1)).updateBook(eq(bookId), any(BookWithInventoryRequestDto.class));
    }

    @Test
    void testUpdateBook_BookNotFound() throws Exception {
        Long bookId = 1L;

        when(bookService.updateBook(eq(bookId), any(BookWithInventoryRequestDto.class)))
                .thenThrow(new ResponseStatusException(NOT_FOUND));

        mockMvc.perform(put("/manage/api/v1/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TestHelper.createWithInventoryRequestDto())))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).updateBook(eq(bookId), any(BookWithInventoryRequestDto.class));
    }

    @Test
    void testDeleteBook() throws Exception {
        Long bookId = 1L;

        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/manage/api/v1/books/" + bookId))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(bookId);
    }

    @Test
    void testGetAllInventories() throws Exception {
        List<InventoryProjectedResponseDto> mockInventories = List.of(TestHelper.createInventoryProjectedResponseDto());

        when(bookService.getAllInventories()).thenReturn(mockInventories);

        mockMvc.perform(get("/manage/api/v1/books/inventories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockInventories)));

        verify(bookService, times(1)).getAllInventories();
    }
}