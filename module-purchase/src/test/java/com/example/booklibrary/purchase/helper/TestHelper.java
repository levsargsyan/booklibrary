package com.example.booklibrary.purchase.helper;

import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;

import java.time.LocalDate;

public class TestHelper {

    public static PurchaseResponseDto createPurchaseResponseDto() {
        PurchaseResponseDto responseDto = new PurchaseResponseDto();
        responseDto.setId(1L);
        responseDto.setVersion(0);
        responseDto.setBookId(1L);
        responseDto.setUserId(1L);
        responseDto.setPurchaseDate(LocalDate.of(2023, 8, 21));
        return responseDto;
    }

    public static PurchaseRequestDto createPurchaseRequestDto() {
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setBookId(1L);
        requestDto.setCount(10);
        return requestDto;
    }
}
