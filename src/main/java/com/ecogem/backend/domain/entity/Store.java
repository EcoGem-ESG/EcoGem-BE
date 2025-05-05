package com.ecogem.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Store {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User 엔티티 준비 전까지는 단순 FK 값으로 매핑
     */
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String address;

    @Column(name = "store_phone", unique = true)
    private String storePhone;

    @Column(name = "owner_phone", unique = true)
    private String ownerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private DeliveryType deliveryType;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

}
