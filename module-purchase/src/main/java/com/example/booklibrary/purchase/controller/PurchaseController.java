package com.example.booklibrary.purchase.controller;

import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponseDto> createPurchase(@Valid @RequestBody PurchaseRequestDto purchaseRequestDto) {
        PurchaseResponseDto savedPurchase = purchaseService.savePurchase(purchaseRequestDto);
        return ResponseEntity.ok(savedPurchase);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByAuthenticatedUser() {
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByAuthenticatedUser();
        return ResponseEntity.ok(purchases);
    }
}
