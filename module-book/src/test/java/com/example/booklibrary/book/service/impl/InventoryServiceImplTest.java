package com.example.booklibrary.book.service.impl;

import com.example.booklibrary.book.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void testGetTotalInventoryBooksCount_NonNullValue() {
        Long expectedCount = 5L;

        when(inventoryRepository.findTotalInventoryBooksCount()).thenReturn(expectedCount);

        Long result = inventoryService.getTotalInventoryBooksCount();

        assertEquals(expectedCount, result);
    }

    @Test
    void testGetTotalInventoryBooksCount_NullValue() {
        when(inventoryRepository.findTotalInventoryBooksCount()).thenReturn(null);

        Long result = inventoryService.getTotalInventoryBooksCount();

        assertEquals(0L, result);
    }
}
