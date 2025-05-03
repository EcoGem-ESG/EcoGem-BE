package com.ecogem.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Company {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User 엔티티 준비 전까지는 단순 FK 값으로 매핑
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String address;

    @Column(name = "manager_name", nullable = false)
    private String managerName;

    @Column(name = "company_phone", nullable = false, unique = true)
    private String companyPhone;
}
