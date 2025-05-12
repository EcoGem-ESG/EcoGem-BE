package com.ecogem.backend.companies.dto;


import lombok.Getter;
import java.util.List;

@Getter
public class CompanyRequestDto {
    private String name;
    private String address;
    private String managerName;
    private String companyPhone;
    private List<String> wasteTypes;
    private Double latitude;
    private Double longitude;
}