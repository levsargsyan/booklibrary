package com.example.booklibrary.recommendation.servcie.impl;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.service.BookService;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.service.PurchaseService;
import com.example.booklibrary.recommendation.servcie.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookService bookService;
    private final PurchaseService purchaseService;

    public static final Integer RECOMMENDED_BOOKS_COUNT = 21;
    public static final Integer PORTION_COUNT = RECOMMENDED_BOOKS_COUNT / 3;

    @Transactional(readOnly = true)
    @Override
    public List<BookWithInventoryResponseDto> getRecommendationsForAuthenticatedUser() {
        List<PurchaseResponseDto> purchases = getLastPurchases();
        Set<Long> purchasedBookIds = purchases
                .stream()
                .map(PurchaseResponseDto::getBookId)
                .collect(Collectors.toSet());

        if (purchases.isEmpty()) {
            return bookService.getAvailableLastAddedBooks(RECOMMENDED_BOOKS_COUNT);
        }

        Set<String> authors = extractAuthorsFromPurchases(purchases);
        Set<String> genres = extractGenresFromPurchases(purchases);

        List<BookWithInventoryResponseDto> recommendedBooksByAuthorsAndGenres = getRecommendedBooksByAuthorsAndGenres(
                authors,
                genres,
                purchasedBookIds);
        List<BookWithInventoryResponseDto> recommendedBooksByAuthors = getRecommendedBooksByAuthors(
                authors,
                purchasedBookIds);
        List<BookWithInventoryResponseDto> recommendedBooksByGenres = getRecommendedBooksByGenres(
                genres,
                purchasedBookIds);

        List<BookWithInventoryResponseDto> recommendedBooks = new ArrayList<>(RECOMMENDED_BOOKS_COUNT);
        recommendedBooks.addAll(recommendedBooksByAuthorsAndGenres);
        recommendedBooks.addAll(recommendedBooksByAuthors);
        recommendedBooks.addAll(recommendedBooksByGenres);

        return appendRandomBooksIfNeeded(recommendedBooks);
    }

    private List<PurchaseResponseDto> getLastPurchases() {
        return purchaseService.getPurchasesByAuthenticatedUser()
                .stream()
                .toList();
    }

    private Set<String> extractAuthorsFromPurchases(List<PurchaseResponseDto> purchases) {
        return purchases
                .stream()
                .map(purchase -> bookService.getBookAuthor(purchase.getBookId()))
                .collect(Collectors.toSet());
    }

    private Set<String> extractGenresFromPurchases(List<PurchaseResponseDto> purchases) {
        return purchases
                .stream()
                .map(purchase -> bookService.getBookGenre(purchase.getBookId()))
                .collect(Collectors.toSet());
    }

    private List<BookWithInventoryResponseDto> getRecommendedBooksByAuthorsAndGenres(
            Set<String> authors,
            Set<String> genres,
            Set<Long> excludedBooks) {
        return bookService.getBooksByAuthorAndGenre(authors, genres)
                .stream()
                .filter(book -> !excludedBooks.contains(book.getId()) && book.getInventory().getCount() >= 1)
                .sorted(Comparator.comparing(BookWithInventoryResponseDto::getPublished).reversed())
                .limit(PORTION_COUNT)
                .toList();
    }

    private List<BookWithInventoryResponseDto> getRecommendedBooksByAuthors(Set<String> authors, Set<Long> excludedBooks) {
        return bookService.getAvailableBooksByAuthors(authors, PORTION_COUNT)
                .stream()
                .filter(book -> !excludedBooks.contains(book.getId()) && book.getInventory().getCount() >= 1)
                .toList();
    }

    private List<BookWithInventoryResponseDto> getRecommendedBooksByGenres(Set<String> genres, Set<Long> excludedBooks) {
        return bookService.getAvailableBooksByGenres(genres, PORTION_COUNT)
                .stream()
                .filter(book -> !excludedBooks.contains(book.getId()) && book.getInventory().getCount() >= 1)
                .toList();
    }

    private List<BookWithInventoryResponseDto> appendRandomBooksIfNeeded(List<BookWithInventoryResponseDto> recommendedBooks) {
        int remainder = RECOMMENDED_BOOKS_COUNT - recommendedBooks.size();

        List<BookWithInventoryResponseDto> finalRecommendations = new ArrayList<>(recommendedBooks);

        if (remainder > 0) {
            List<BookWithInventoryResponseDto> randomBooks = bookService.getAvailableLastAddedBooks(remainder);
            finalRecommendations.addAll(randomBooks);
        }

        return finalRecommendations;
    }
}
