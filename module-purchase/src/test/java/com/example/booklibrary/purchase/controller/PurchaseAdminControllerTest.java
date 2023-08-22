package com.example.booklibrary.purchase.controller;

import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.helper.TestHelper;
import com.example.booklibrary.purchase.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.example.booklibrary.purchase.helper.TestHelper.createPurchaseResponseDto;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PurchaseAdminController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PurchaseAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    @Test
    void testGetAllPurchases() throws Exception {
        List<PurchaseResponseDto> emptyList = Collections.emptyList();

        when(purchaseService.getAllPurchases()).thenReturn(emptyList);

        mockMvc.perform(get("/manage/api/v1/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(purchaseService, times(1)).getAllPurchases();
    }

    @Test
    void testGetPurchasesByUserId() throws Exception {
        List<PurchaseResponseDto> purchaseResponseDtoList = List.of(createPurchaseResponseDto());
        Long userId = 1L;

        when(purchaseService.getPurchasesByUserId(userId)).thenReturn(purchaseResponseDtoList);

        mockMvc.perform(get("/manage/api/v1/purchases/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(purchaseResponseDtoList)));

        verify(purchaseService, times(1)).getPurchasesByUserId(userId);
    }

    @Test
    void testGetPurchasesByBookId() throws Exception {
        List<PurchaseResponseDto> purchaseResponseDtoList = List.of(createPurchaseResponseDto());
        Long bookId = 1L;

        when(purchaseService.getPurchasesByBookId(bookId)).thenReturn(purchaseResponseDtoList);

        mockMvc.perform(get("/manage/api/v1/purchases/book/" + bookId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(purchaseResponseDtoList)));

        verify(purchaseService, times(1)).getPurchasesByBookId(bookId);
    }

    @Test
    void testGetTotalPurchasedBooksCount() throws Exception {
        Long totalCount = 100L;

        when(purchaseService.getTotalPurchasedBooksCount()).thenReturn(totalCount);

        mockMvc.perform(get("/manage/api/v1/purchases/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));

        verify(purchaseService, times(1)).getTotalPurchasedBooksCount();
    }
}
