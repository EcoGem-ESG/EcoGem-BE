package com.ecogem.backend.contract.entity;

import com.ecogem.backend.company.domain.Company;
import com.ecogem.backend.store.domain.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "contracts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "store_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

