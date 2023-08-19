package com.example.booklibrary.book.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InventoryResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 2365853624754526752L;

    private Long id;

    private Integer version;

    private Integer count;
}
