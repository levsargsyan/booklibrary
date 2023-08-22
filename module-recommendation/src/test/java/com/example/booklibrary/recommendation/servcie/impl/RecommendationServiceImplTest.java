package com.example.booklibrary.recommendation.servcie.impl;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.service.BookService;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Mock
    private BookService bookService;

    @Mock
    private PurchaseService purchaseService;

    @Test
    void testGetRecommendationsForAuthenticatedUser_NoPurchases() {
        when(purchaseService.getPurchasesByAuthenticatedUser()).thenReturn(Collections.emptyList());
        when(bookService.getAvailableLastAddedBooks(RecommendationServiceImpl.RECOMMENDED_BOOKS_COUNT)).thenReturn(new ArrayList<>());

        List<BookWithInventoryResponseDto> recommendations = recommendationService.getRecommendationsForAuthenticatedUser();

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void testGetRecommendationsForAuthenticatedUser_WithPurchases() {
        PurchaseResponseDto purchase = new PurchaseResponseDto();
        purchase.setBookId(1L);
        List<PurchaseResponseDto> purchases = Collections.singletonList(purchase);
        BookWithInventoryResponseDto bookWithInventory = new BookWithInventoryResponseDto();
        bookWithInventory.setId(1L);

        when(purchaseService.getPurchasesByAuthenticatedUser()).thenReturn(purchases);
        when(bookService.getBookAuthor(anyLong())).thenReturn("AuthorName");
        when(bookService.getBookGenre(anyLong())).thenReturn("GenreName");
        when(bookService.getBooksByAuthorAndGenre(anySet(), anySet())).thenReturn(new ArrayList<>());
        when(bookService.getAvailableBooksByAuthors(anySet(), anyInt())).thenReturn(new ArrayList<>());
        when(bookService.getAvailableBooksByGenres(anySet(), anyInt())).thenReturn(new ArrayList<>());
        when(bookService.getAvailableLastAddedBooks(anyInt())).thenReturn(Collections.singletonList(bookWithInventory));

        List<BookWithInventoryResponseDto> recommendations = recommendationService.getRecommendationsForAuthenticatedUser();

        assertFalse(recommendations.isEmpty());
        assertEquals(1, recommendations.size());
        assertEquals(1L, recommendations.get(0).getId());
    }
}
