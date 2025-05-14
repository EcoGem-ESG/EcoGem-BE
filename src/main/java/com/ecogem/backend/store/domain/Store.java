package com.ecogem.backend.store.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "stores")
public class Store {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String storePhone;
    private String ownerPhone;

    @Column private Double latitude;
    @Column private Double longitude;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    public enum DeliveryType {
        SMALL, MEDIUM, LARGE, IRREGULAR
    }

    // ← 이 메서드를 추가하세요
    public void updateProfile(String address,
                              String storePhone,
                              String ownerPhone,
                              DeliveryType deliveryType) {
        this.address = address;
        this.storePhone = storePhone;
        this.ownerPhone  = ownerPhone;
        this.deliveryType = deliveryType;
    }
}
