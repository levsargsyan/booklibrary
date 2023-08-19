package com.example.booklibrary.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InventoryRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 3820620042576725354L;

    @Null
    private Long id;

    @Null
    private Integer version;

    @NotNull
    private Integer count;
}
