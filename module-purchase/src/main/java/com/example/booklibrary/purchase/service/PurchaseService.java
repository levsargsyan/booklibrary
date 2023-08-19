package com.example.booklibrary.purchase.service;

import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;

import java.util.List;

public interface PurchaseService {

    PurchaseResponseDto savePurchase(PurchaseRequestDto purchaseRequestDto);

    List<PurchaseResponseDto> getAllPurchases();

    List<PurchaseResponseDto> getPurchasesByUserId(Long userId);

    List<PurchaseResponseDto> getPurchasesByAuthenticatedUser();

    List<PurchaseResponseDto> getPurchasesByBookId(Long bookId);

    Long getTotalPurchasedBooksCount();
}
