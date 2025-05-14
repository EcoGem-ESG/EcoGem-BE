package com.ecogem.backend.reports.dto;


import lombok.Getter;
import java.time.LocalDate;

@Getter
public class ReportRequestDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
}