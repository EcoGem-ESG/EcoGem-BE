package com.ecogem.backend.company.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;              // 업체명
    private String address;           // 업체 주소
    private String managerName;       // 담당자명
    private String companyPhone;      // 업체 전화번호

    @Column private Double latitude;
    @Column private Double longitude;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_waste_types", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "waste_type")
    private List<String> wasteTypes;  // 폐기물 종류 목록 (예: edible_oil, animal_fat 등)


}
