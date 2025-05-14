package com.ecogem.backend.mypage.dto;

import com.ecogem.backend.store.domain.Store;
import lombok.Getter;

@Getter
public class StoreMypageResponse {

    private final String name;
    private final String address;
    private final String storePhone;
    private final String ownerPhone;
    private final String deliveryType;

    public StoreMypageResponse(Store store) {
        this.name = store.getName();
        this.address = store.getAddress();
        this.storePhone = store.getStorePhone();
        this.ownerPhone = store.getOwnerPhone();
        this.deliveryType = store.getDeliveryType().name(); // Enum → 문자열
    }
}
