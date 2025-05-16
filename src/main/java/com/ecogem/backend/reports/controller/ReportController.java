package com.ecogem.backend.reports.controller;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.reports.dto.ReportCreateResponse;
import com.ecogem.backend.reports.dto.ReportRequestDto;
import com.ecogem.backend.reports.service.ReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generates a report for the authenticated user by extracting the userId, role,
     * and company/store name from the user's details.
     */
    @PostMapping
    public ResponseEntity<ReportCreateResponse> createReport(
            @AuthenticationPrincipal User user,
            @RequestBody ReportRequestDto request
    ) {
        // Convert auth.domain.Role to domain.entity.Role
        Role role = Role.valueOf(user.getRole().name());

        // Determine the company or store name based on role
        String storeName = (role == Role.COMPANY_WORKER)
                ? user.getCompany().getName()
                : user.getStore().getName();

        // Generate the report file
        String filePath = reportService.generateReport(
                user.getId(),
                role,
                storeName,
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity.status(201)
                .body(new ReportCreateResponse(true, 201, "REPORT_CREATE_SUCCESS", filePath));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String filename) throws IOException {
        File file = new File("/tmp/" + filename);  // Path where report files are stored

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
