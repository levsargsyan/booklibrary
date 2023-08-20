package com.example.booklibrary.recommendation.controller;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.recommendation.servcie.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<BookWithInventoryResponseDto>> getAllBooks() {
        return ResponseEntity.ok(recommendationService.getRecommendationsForAuthenticatedUser());
    }
}
