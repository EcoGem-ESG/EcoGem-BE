package com.ecogem.backend.mypage.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MypageUpdateRequest {

    // 공통
    private String address;

    // 업체용 필드
    private String managerName;
    private String companyPhone;
    private List<String> wasteTypes;

    // 가게용 필드
    private String storePhone;
    private String ownerPhone;
    private String deliveryType;
}
