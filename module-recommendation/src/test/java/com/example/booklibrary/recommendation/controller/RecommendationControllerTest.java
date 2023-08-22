package com.example.booklibrary.recommendation.controller;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryResponseDto;
import com.example.booklibrary.recommendation.servcie.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = RecommendationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void testGetAllBooks() throws Exception {
        List<BookWithInventoryResponseDto> bookWithInventoryResponseDtoList = List.of(createBookWithInventoryResponseDto());

        when(recommendationService.getRecommendationsForAuthenticatedUser()).thenReturn(bookWithInventoryResponseDtoList);

        mockMvc.perform(get("/api/v1/recommendations"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookWithInventoryResponseDtoList)));

        verify(recommendationService, times(1)).getRecommendationsForAuthenticatedUser();
    }

    private BookWithInventoryResponseDto createBookWithInventoryResponseDto() {
        BookWithInventoryResponseDto responseDto = new BookWithInventoryResponseDto();
        responseDto.setTitle("Alice in Wonderland");
        responseDto.setAuthor("Lewis Carroll");
        responseDto.setGenre("Fantasy");
        responseDto.setDescription("A classic tale of a girl named Alice...");
        responseDto.setIsbn("9797822925048");
        responseDto.setImage("http://placeimg.com/480/640/any");
        responseDto.setPublished(LocalDate.of(1865, 11, 26));
        responseDto.setPublisher("Macmillan");

        InventoryResponseDto inventoryDto = new InventoryResponseDto();
        inventoryDto.setCount(15);

        responseDto.setInventory(inventoryDto);

        return responseDto;
    }
}
