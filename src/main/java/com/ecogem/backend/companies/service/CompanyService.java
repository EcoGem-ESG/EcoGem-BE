package com.ecogem.backend.companies.service;

import com.ecogem.backend.auth.domain.Status;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.repositorty.UserRepository;
import com.ecogem.backend.companies.domain.Company;
import com.ecogem.backend.companies.dto.CompanyRequestDto;
import com.ecogem.backend.companies.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public Long registerCompany(Long userId, CompanyRequestDto request) {
        // 1. 회사 등록
        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .managerName(request.getManagerName())
                .companyPhone(request.getCompanyPhone())
                .wasteTypes(request.getWasteTypes())
                .build();
        companyRepository.save(company);

        // 2. 유저 상태 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        user.setStatus(Status.COMPLETE);
        user.setCompany(company);
        userRepository.save(user);

        return company.getId();
    }
}
