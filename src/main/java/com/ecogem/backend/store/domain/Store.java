package com.ecogem.backend.store.domain;


import jakarta.persistence.*;
import lombok.*;
import com.ecogem.backend.auth.domain.User;

import java.util.List;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "store")
    private List<User> users;

    public enum DeliveryType {
        SMALL, LARGE
    }
}
