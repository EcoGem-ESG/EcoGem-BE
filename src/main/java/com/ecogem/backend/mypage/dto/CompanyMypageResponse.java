package com.ecogem.backend.mypage.dto;

import com.ecogem.backend.companies.domain.Company;
import lombok.Getter;

import java.util.List;

@Getter
public class CompanyMypageResponse {

    private final String name;
    private final String address;
    private final String managerName;
    private final String companyPhone;
    private final List<String> wasteTypes;

    public CompanyMypageResponse(Company company) {
        this.name = company.getName();
        this.address = company.getAddress();
        this.managerName = company.getManagerName();
        this.companyPhone = company.getCompanyPhone();
        this.wasteTypes = company.getWasteTypes();
    }
}
