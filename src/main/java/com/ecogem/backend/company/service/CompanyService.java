package com.ecogem.backend.company.service;

import com.ecogem.backend.auth.domain.Status;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.repositorty.UserRepository;
import com.ecogem.backend.company.domain.Company;
import com.ecogem.backend.company.dto.CompanyRequestDto;
import com.ecogem.backend.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Long registerCompany(Long userId, CompanyRequestDto request) {
        // 1. Register the company
        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .managerName(request.getManagerName())
                .companyPhone(request.getCompanyPhone())
                .wasteTypes(request.getWasteTypes())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        companyRepository.save(company);

        // 2. Update user status and associate the company
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setStatus(Status.COMPLETE);
        user.setCompany(company);
        userRepository.save(user);

        return company.getId();
    }
}
