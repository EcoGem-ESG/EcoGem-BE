package com.ecogem.backend.reports.dto;

import com.ecogem.backend.domain.entity.Role;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReportRequestDto {
    private Long userId;
    private Role role;
    private String storeName;
    private LocalDate startDate;
    private LocalDate endDate;
}
