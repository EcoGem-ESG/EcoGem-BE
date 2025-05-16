package com.ecogem.backend.mypage.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MypageUpdateRequest {

    // Common
    private String address;

    // Company-specific fields
    private String managerName;
    private String companyPhone;
    private List<String> wasteTypes;

    // Store-specific fields
    private String storePhone;
    private String ownerPhone;
    private String deliveryType;
}