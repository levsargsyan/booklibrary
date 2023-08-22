package com.example.booklibrary.book.controller;

import com.example.booklibrary.book.dto.BookResponseDto;
import com.example.booklibrary.book.dto.search.BookSearchCommand;
import com.example.booklibrary.book.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.booklibrary.book.helper.TestHelper.createBookResponseDto;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void testGetBookById() throws Exception {
        Long bookId = 1L;
        BookResponseDto mockBook = createBookResponseDto();

        when(bookService.getBook(bookId)).thenReturn(mockBook);

        mockMvc.perform(get("/api/v1/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockBook)));

        verify(bookService, times(1)).getBook(bookId);
    }

    @Test
    void testGetAllBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookResponseDto> page = new PageImpl<>(List.of(createBookResponseDto()), pageable, 1);

        when(bookService.getBooksPaginated(pageable)).thenReturn(page);

        mockMvc.perform(get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
        verify(bookService, times(1)).getBooksPaginated(pageable);
    }

    @Test
    void testSearchBooks() throws Exception {
        BookSearchCommand searchCommand = new BookSearchCommand();
        Pageable pageable = PageRequest.of(0, 5);
        Page<BookResponseDto> mockBooksPaginatedDto = new PageImpl<>(List.of(createBookResponseDto()), pageable, 1);

        when(bookService.searchBooks(searchCommand, pageable)).thenReturn(mockBooksPaginatedDto);

        mockMvc.perform(post("/api/v1/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCommand))
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).searchBooks(searchCommand, pageable);
    }
}
