package com.example.booklibrary.purchase.controller;

import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseAdminController {

    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases() {
        List<PurchaseResponseDto> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByUserId(@PathVariable Long userId) {
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByUserId(userId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByBookId(@PathVariable Long bookId) {
        List<PurchaseResponseDto> purchases = purchaseService.getPurchasesByBookId(bookId);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalPurchasedBooksCount() {
        Long totalCount = purchaseService.getTotalPurchasedBooksCount();
        return ResponseEntity.ok(totalCount);
    }
}
