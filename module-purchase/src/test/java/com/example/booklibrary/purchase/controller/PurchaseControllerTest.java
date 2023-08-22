package com.example.booklibrary.purchase.controller;

import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PurchaseController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    @Test
    void testCreatePurchase() throws Exception {
        PurchaseRequestDto requestDto = createPurchaseRequestDto();
        PurchaseResponseDto responseDto = createPurchaseResponseDto();

        when(purchaseService.savePurchase(any(PurchaseRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));

        verify(purchaseService, times(1)).savePurchase(any(PurchaseRequestDto.class));
    }

    @Test
    void testGetPurchasesByAuthenticatedUser() throws Exception {
        List<PurchaseResponseDto> mockPurchases = List.of(createPurchaseResponseDto());

        when(purchaseService.getPurchasesByAuthenticatedUser()).thenReturn(mockPurchases);

        mockMvc.perform(get("/api/v1/purchases/user"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockPurchases)));

        verify(purchaseService, times(1)).getPurchasesByAuthenticatedUser();
    }

    private PurchaseRequestDto createPurchaseRequestDto() {
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setBookId(1L);
        requestDto.setCount(10);
        return requestDto;
    }

    private PurchaseResponseDto createPurchaseResponseDto() {
        PurchaseResponseDto responseDto = new PurchaseResponseDto();
        responseDto.setId(1L);
        responseDto.setVersion(0);
        responseDto.setBookId(1L);
        responseDto.setUserId(1L);
        responseDto.setPurchaseDate(LocalDate.of(2023, 8, 21));
        return responseDto;
    }
}
