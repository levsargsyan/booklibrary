package com.example.booklibrary.purchase.service.impl;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.service.BookService;
import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.mapper.PurchaseMapper;
import com.example.booklibrary.purchase.model.Purchase;
import com.example.booklibrary.purchase.repository.PurchaseRepository;
import com.example.booklibrary.purchase.service.PurchaseService;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final UserService userService;
    private final BookService bookService;

    @Transactional
    @Override
    public PurchaseResponseDto savePurchase(PurchaseRequestDto purchaseDto) {
        BookWithInventoryResponseDto bookWithInventory = bookService.getBookWithInventory(purchaseDto.getBookId());
        checkBookInventory(bookWithInventory);
        purchaseDto.setUserId(getCurrentUserId());

        Purchase purchase = purchaseRepository.save(purchaseMapper.purchaseRequestDtoToPurchase(purchaseDto));
        bookService.decrementBookCount(bookWithInventory.getInventory().getId(), purchaseDto.getCount());
        return purchaseMapper.purchaseToPurchaseResponseDto(purchase);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PurchaseResponseDto> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return purchaseMapper.purchasesToPurchaseResponseDtos(purchases);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PurchaseResponseDto> getPurchasesByUserId(Long userId) {
        List<Purchase> purchases = purchaseRepository.findByUserId(userId);
        return purchaseMapper.purchasesToPurchaseResponseDtos(purchases);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PurchaseResponseDto> getPurchasesByAuthenticatedUser() {
        Long authenticatedUserId = getCurrentUserId();
        List<Purchase> purchases = purchaseRepository.findByUserId(authenticatedUserId);
        return purchaseMapper.purchasesToPurchaseResponseDtos(purchases);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PurchaseResponseDto> getPurchasesByBookId(Long bookId) {
        List<Purchase> purchases = purchaseRepository.findByBookId(bookId);
        return purchaseMapper.purchasesToPurchaseResponseDtos(purchases);
    }

    @Transactional(readOnly = true)
    @Override
    public Long getTotalPurchasedBooksCount() {
        Long totalCount = purchaseRepository.findTotalPurchasedBooksCount();
        return Objects.isNull(totalCount) ? 0 : totalCount;
    }

    private void checkBookInventory(BookWithInventoryResponseDto booResponseDto) {
        if (booResponseDto.getInventory().getCount() <= 0) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "This book is currently out of stock.");
        }
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String email) {
            UserResponseDto userResponseDto = userService.getUserByEmail(email);
            return userResponseDto.getId();
        } else {
            throw new ResponseStatusException(
                    UNAUTHORIZED,
                    "Authentication required to proceed with purchase");
        }
    }
}
