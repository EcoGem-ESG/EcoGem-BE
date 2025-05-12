package com.ecogem.backend.reports.controller;

import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.reports.dto.ReportRequestDto;
import com.ecogem.backend.reports.dto.ReportCreateResponse;
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
     * 로그인된 사용자 정보에서 userId, role, company/store name을 가져와
     * 해당 대상의 보고서를 생성합니다.
     */
    @PostMapping
    public ResponseEntity<ReportCreateResponse> createReport(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody ReportRequestDto request
    ) {
        // 1) 로그인 사용자 정보
        Long userId = principal.getUser().getId();
        Role role = principal.getUser().getRole();
        String targetName;

        if (role == Role.COMPANY_WORKER) {
            targetName = principal.getUser().getCompany().getName();
        } else {
            targetName = principal.getUser().getStore().getName();
        }

        // 2) 보고서 생성
        String filePath = reportService.generateReport(
                userId,
                role,
                targetName,
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity.status(201)
                .body(new ReportCreateResponse(
                        true,
                        201,
                        "REPORT_CREATE_SUCCESS",
                        filePath
                ));
    }

    /**
     * 생성된 PDF를 다운로드합니다.
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String filename) throws IOException {
        // 실제 저장 디렉토리에 맞춰 경로 수정 가능
        File file = new File("src/main/resources/reports/" + filename);
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