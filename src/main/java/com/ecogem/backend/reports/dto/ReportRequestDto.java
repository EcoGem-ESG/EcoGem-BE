package com.ecogem.backend.reports.dto;


import com.ecogem.backend.auth.domain.Role;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReportRequestDto {
    private Long userId;
    private Role role;

    private LocalDate startDate;
    private LocalDate endDate;
}
