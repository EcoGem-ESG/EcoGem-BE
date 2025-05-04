package com.ecogem.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "store_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA용 기본 생성자
@AllArgsConstructor(access = AccessLevel.PRIVATE)    // Builder용 생성자
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}

