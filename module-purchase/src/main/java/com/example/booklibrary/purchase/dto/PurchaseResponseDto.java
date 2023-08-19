package com.example.booklibrary.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseResponseDto {

    private Long id;

    private Integer version;

    private Long userId;

    private Long bookId;

    private Integer count;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
}
