package com.ecogem.backend.company.controller;


import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.company.service.CompanyService;
import com.ecogem.backend.company.dto.CompanyRequestDto;
import com.ecogem.backend.company.dto.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDto> registerCompany(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody CompanyRequestDto dto
    ) {
        Long userId = principal.getUser().getId();
        Long companyId = companyService.registerCompany(userId, dto);

        CompanyResponseDto response = new CompanyResponseDto(
                true,
                200,
                "COMPANY_REGISTER_SUCCESS",
                new CompanyResponseDto.Data(companyId)
        );

        return ResponseEntity.ok(response);
    }
}