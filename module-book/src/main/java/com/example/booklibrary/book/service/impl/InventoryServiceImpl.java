package com.example.booklibrary.book.service.impl;

import com.example.booklibrary.book.repository.InventoryRepository;
import com.example.booklibrary.book.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @Override
    public Long getTotalInventoryBooksCount() {
        Long totalCount = inventoryRepository.findTotalInventoryBooksCount();
        return Objects.isNull(totalCount) ? 0 : totalCount;
    }
}
