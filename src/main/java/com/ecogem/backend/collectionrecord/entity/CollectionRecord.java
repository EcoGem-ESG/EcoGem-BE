package com.ecogem.backend.collectionrecord.entity;

import com.ecogem.backend.domain.entity.Company;
import com.ecogem.backend.domain.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "collection_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CollectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "collected_at", nullable = false)
    private LocalDate collectedAt;

    @Column(name = "collected_by", nullable = false)
    private String collectedBy;

    @Column(name = "volume_liter", precision = 5, scale = 2, nullable = false)
    private BigDecimal volumeLiter;

    @Column(name = "price_per_liter", nullable = false)
    private Integer pricePerLiter;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

}
