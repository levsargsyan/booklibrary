package com.example.booklibrary.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseRequestDto {

    @Null
    private Long id;

    @Null
    private Integer version;

    @Null
    private Long userId;

    @NotNull
    private Long bookId;

    @NotNull
    @Min(1)
    private Integer count;

    @Null
    private LocalDate purchaseDate;
}
