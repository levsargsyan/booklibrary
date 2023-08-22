package com.example.booklibrary.purchase.service.impl;

import com.example.booklibrary.book.dto.BookWithInventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryResponseDto;
import com.example.booklibrary.book.service.BookService;
import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.mapper.PurchaseMapper;
import com.example.booklibrary.purchase.model.Purchase;
import com.example.booklibrary.purchase.repository.PurchaseRepository;
import com.example.booklibrary.security.constant.Role;
import com.example.booklibrary.security.dto.UserResponseDto;
import com.example.booklibrary.security.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PurchaseMapper purchaseMapper;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    @Test
    void testSavePurchase() {
        setupSecurityContext();
        PurchaseRequestDto requestDto = new PurchaseRequestDto();
        requestDto.setBookId(1L);
        requestDto.setCount(1);
        BookWithInventoryResponseDto bookResponseDto = new BookWithInventoryResponseDto();
        InventoryResponseDto inventoryResponseDto = new InventoryResponseDto();
        inventoryResponseDto.setId(1L);
        inventoryResponseDto.setVersion(0);
        inventoryResponseDto.setCount(5);
        bookResponseDto.setInventory(inventoryResponseDto);
        Purchase savedPurchase = new Purchase();

        when(bookService.getBookWithInventory(anyLong())).thenReturn(bookResponseDto);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);
        when(userService.getUserByEmail(anyString())).thenReturn(createUserResponseDto());

        when(purchaseMapper.purchaseRequestDtoToPurchase(requestDto)).thenReturn(new Purchase());
        when(purchaseMapper.purchaseToPurchaseResponseDto(savedPurchase)).thenReturn(new PurchaseResponseDto());

        purchaseService.savePurchase(requestDto);

        verify(bookService).decrementBookCount(anyLong(), anyInt());
        verify(purchaseMapper).purchaseRequestDtoToPurchase(any(PurchaseRequestDto.class));
        verify(purchaseMapper).purchaseToPurchaseResponseDto(any(Purchase.class));
    }

    @Test
    void testGetAllPurchases() {
        List<Purchase> purchases = List.of(new Purchase());
        when(purchaseRepository.findAll()).thenReturn(purchases);

        purchaseService.getAllPurchases();

        verify(purchaseMapper).purchasesToPurchaseResponseDtos(purchases);
    }

    @Test
    void testGetAllPurchasesCount() {
        when(purchaseRepository.count()).thenReturn(5L);

        Long count = purchaseService.getAllPurchasesCount();

        assertEquals(5L, count);
    }

    @Test
    void testGetPurchasesByUserId() {
        List<Purchase> purchases = List.of(new Purchase());
        when(purchaseRepository.findByUserId(anyLong())).thenReturn(purchases);

        purchaseService.getPurchasesByUserId(1L);

        verify(purchaseMapper).purchasesToPurchaseResponseDtos(purchases);
    }

    @Test
    void testGetPurchasesByAuthenticatedUser() {
        setupSecurityContext();

        Long authenticatedUserId = 1L;
        List<Purchase> samplePurchases = Arrays.asList(
                createPurchase(1L),
                createPurchase(2L),
                createPurchase(3L)
        );

        List<PurchaseResponseDto> expectedResponse = Arrays.asList(
                createPurchaseResponseDto(1L),
                createPurchaseResponseDto(2L),
                createPurchaseResponseDto(3L)
        );

        when(userService.getUserByEmail(anyString())).thenReturn(createUserResponseDto());
        when(purchaseRepository.findByUserId(authenticatedUserId)).thenReturn(samplePurchases);
        when(purchaseMapper.purchasesToPurchaseResponseDtos(samplePurchases)).thenReturn(expectedResponse);

        List<PurchaseResponseDto> result = purchaseService.getPurchasesByAuthenticatedUser();

        assertEquals(expectedResponse, result, "Returned purchase list should match expected response.");
        verify(purchaseRepository).findByUserId(authenticatedUserId);
        verify(purchaseMapper).purchasesToPurchaseResponseDtos(samplePurchases);
    }

    @Test
    void testGetPurchasesByBookId() {
        Long bookId = 1L;
        List<Purchase> samplePurchases = Arrays.asList(
                createPurchase(1L),
                createPurchase(2L),
                createPurchase(3L)
        );

        List<PurchaseResponseDto> expectedResponse = Arrays.asList(
                createPurchaseResponseDto(1L),
                createPurchaseResponseDto(2L),
                createPurchaseResponseDto(3L)
        );

        when(purchaseRepository.findByBookId(bookId)).thenReturn(samplePurchases);
        when(purchaseMapper.purchasesToPurchaseResponseDtos(samplePurchases)).thenReturn(expectedResponse);

        List<PurchaseResponseDto> result = purchaseService.getPurchasesByBookId(bookId);

        assertEquals(expectedResponse, result, "Returned purchase list should match expected response.");
        verify(purchaseRepository).findByBookId(bookId);
        verify(purchaseMapper).purchasesToPurchaseResponseDtos(samplePurchases);
    }

    @Test
    void testGetTotalPurchasedBooksCount() {
        Long expectedCount = 10L;

        when(purchaseRepository.findTotalPurchasedBooksCount()).thenReturn(expectedCount);

        Long result = purchaseService.getTotalPurchasedBooksCount();

        assertEquals(expectedCount, result, "Returned count should match expected value.");
        verify(purchaseRepository).findTotalPurchasedBooksCount();
    }

    @Test
    void testGetTotalPurchasedBooksCountWhenNull() {
        when(purchaseRepository.findTotalPurchasedBooksCount()).thenReturn(null);

        Long result = purchaseService.getTotalPurchasedBooksCount();

        assertEquals(0L, result, "Returned count should be 0 when repository returns null.");
        verify(purchaseRepository).findTotalPurchasedBooksCount();
    }


    private void setupSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn("test@email.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        UserResponseDto mockUser = createUserResponseDto();

        when(userService.getUserByEmail(anyString())).thenReturn(mockUser);
    }

    private UserResponseDto createUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(1L);
        userResponseDto.setVersion(1);
        userResponseDto.setName("John Doe");
        userResponseDto.setPhone("+123456789");
        userResponseDto.setEmail("john.doe@example.com");
        userResponseDto.setAddress("123 Main St, CityTown");
        userResponseDto.setPostalZip("12345");
        userResponseDto.setCountry("FictionLand");
        userResponseDto.setPan("ABCDE1234F");
        userResponseDto.setExpDate(LocalDate.of(2025, 1, 1));
        userResponseDto.setRole(Role.ADMIN);

        return userResponseDto;
    }

    private Purchase createPurchase(Long id) {
        Purchase purchase = new Purchase();

        purchase.setId(id);
        purchase.setVersion(1);
        purchase.setBookId(1L);
        purchase.setUserId(1L);
        purchase.setPurchaseDate(LocalDate.of(2023, 8, 22));

        return purchase;
    }

    private PurchaseResponseDto createPurchaseResponseDto(Long id) {
        PurchaseResponseDto purchaseResponseDto = new PurchaseResponseDto();

        purchaseResponseDto.setId(id);
        purchaseResponseDto.setVersion(1);
        purchaseResponseDto.setBookId(1L);
        purchaseResponseDto.setUserId(1L);
        purchaseResponseDto.setPurchaseDate(LocalDate.of(2023, 8, 22));

        return purchaseResponseDto;
    }
}
