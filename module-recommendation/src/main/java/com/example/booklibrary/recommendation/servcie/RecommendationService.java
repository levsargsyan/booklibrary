package com.example.booklibrary.recommendation.servcie;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;

import java.util.List;

public interface RecommendationService {

    List<BookWithInventoryResponseDto> getRecommendationsForAuthenticatedUser();
}
