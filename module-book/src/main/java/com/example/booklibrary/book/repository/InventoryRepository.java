package com.example.booklibrary.book.repository;

import com.example.booklibrary.book.dto.InventoryProjectedResponseDto;
import com.example.booklibrary.book.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT new com.example.booklibrary.book.dto.InventoryProjectedResponseDto(i.id, i.version, i.count, b.id, b.isbn) FROM Inventory i JOIN i.book b")
    List<InventoryProjectedResponseDto> findAllProjectedBy();
}
