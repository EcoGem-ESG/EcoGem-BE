package com.ecogem.backend.companies.controller;


import com.ecogem.backend.companies.service.CompanyService;
import com.ecogem.backend.companies.dto.CompanyRequestDto;
import com.ecogem.backend.companies.dto.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/{userId}")
    public ResponseEntity<CompanyResponseDto> registerCompany(
            @PathVariable Long userId,
            @RequestBody CompanyRequestDto requestDto
    ) {
        Long companyId = companyService.registerCompany(userId, requestDto);

        CompanyResponseDto response = new CompanyResponseDto(
                true,
                200,
                "COMPANY_REGISTER_SUCCESS",
                new CompanyResponseDto.Data(companyId)
        );

        return ResponseEntity.ok(response);
    }
}