package com.example.booklibrary.purchase.repository;

import com.example.booklibrary.purchase.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(Long userId);

    List<Purchase> findByBookId(Long bookId);

    @Query("SELECT SUM(p.count) FROM Purchase p")
    Long findTotalPurchasedBooksCount();
}
