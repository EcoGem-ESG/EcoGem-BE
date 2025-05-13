package com.ecogem.backend.company.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyResponseDto {
    private boolean success;
    private int code;
    private String message;
    private Data data;

    @Getter
    @AllArgsConstructor
    public static class Data {
        private Long companyId;
    }
}
