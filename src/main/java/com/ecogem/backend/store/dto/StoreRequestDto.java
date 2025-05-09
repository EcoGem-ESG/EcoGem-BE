package com.ecogem.backend.store.dto;


import com.ecogem.backend.store.domain.Store.DeliveryType;
import lombok.Getter;

@Getter
public class StoreRequestDto {
    private String name;
    private String address;
    private String storePhone;
    private String ownerPhone;
    private DeliveryType deliveryType;
}