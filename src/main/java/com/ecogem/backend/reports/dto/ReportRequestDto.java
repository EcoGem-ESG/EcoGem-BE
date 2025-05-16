package com.ecogem.backend.reports.dto;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
public class ReportRequestDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}


