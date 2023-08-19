package com.example.booklibrary.book.service;

import com.example.booklibrary.book.dto.InventoryResponseDto;
import com.example.booklibrary.book.dto.InventoryRequestDto;

import java.util.List;

public interface InventoryService {

    List<InventoryResponseDto> getAllInventories();

    InventoryResponseDto getInventory(Long id);

    InventoryResponseDto saveInventory(InventoryRequestDto inventoryRequestDto);

    InventoryResponseDto updateInventory(Long id, InventoryRequestDto inventoryRequestDto);

    void deleteInventory(Long id);
}
