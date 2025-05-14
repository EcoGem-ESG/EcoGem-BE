package com.ecogem.backend.reports.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportCreateResponse {
    
    private boolean success;
    private int code;
    private String message;
    private String reportFilePath;
}
