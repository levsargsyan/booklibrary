package com.example.booklibrary.book.service.impl;

import com.example.booklibrary.book.dto.InventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryRequestDto;
import com.example.booklibrary.book.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    @Transactional(readOnly = true)
    @Override
    public List<InventoryResponseDto> getAllInventories() {
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryResponseDto getInventory(Long id) {
        return null;
    }

    @Transactional
    @Override
    public InventoryResponseDto saveInventory(InventoryRequestDto inventoryRequestDto) {
        return null;
    }

    @Transactional
    @Override
    public InventoryResponseDto updateInventory(Long id, InventoryRequestDto inventoryRequestDto) {
        return null;
    }

    @Transactional
    @Override
    public void deleteInventory(Long id) {

    }
}
