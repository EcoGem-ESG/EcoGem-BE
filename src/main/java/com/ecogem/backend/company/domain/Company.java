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

    private String name;
    private String address;
    private String managerName;
    private String companyPhone;

    @Column private Double latitude;
    @Column private Double longitude;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_waste_types", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "waste_type")
    private List<String> wasteTypes;


}
