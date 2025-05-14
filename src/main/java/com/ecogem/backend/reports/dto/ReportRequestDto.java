package com.ecogem.backend.reports.dto;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ReportRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
