package com.ecogem.backend.reports.controller;

import com.ecogem.backend.domain.entity.Role;
import com.ecogem.backend.reports.dto.ReportRequestDto;
import com.ecogem.backend.reports.service.ReportService;
import com.ecogem.backend.reports.dto.ReportCreateResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDto request) {
        String filePath = reportService.generateReport(
                request.getUserId(),
                request.getRole(),
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity.status(201).body(
                new ReportCreateResponse(true, 201, "REPORT_CREATE_SUCCESS", filePath)
        );
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String filename) throws IOException {
        File file = new File("src/main/resources/reports/" + filename);  // 경로는 네가 저장한 위치에 따라 조절

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }
}
