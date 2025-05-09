package com.ecogem.backend.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponseDto {
    private boolean success;
    private int code;
    private String message;
    private Long storeId;
}