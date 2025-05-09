package com.ecogem.backend.store.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String storePhone;
    private String ownerPhone;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    public enum DeliveryType {
        SMALL, LARGE
    }
}
