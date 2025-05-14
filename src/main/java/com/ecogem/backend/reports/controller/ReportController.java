package com.ecogem.backend.reports.controller;


import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.reports.dto.ReportCreateResponse;
import com.ecogem.backend.reports.dto.ReportRequestDto;
import com.ecogem.backend.reports.service.ReportService;
import com.ecogem.backend.domain.entity.Role; //  domain.entity.Role

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

            @AuthenticationPrincipal User user,
            @RequestBody ReportRequestDto request
    ) {
        // 🔁 auth.domain.Role → domain.entity.Role 변환
        Role role = Role.valueOf(user.getRole().name());

        // 🔍 회사 or 가게 이름 설정
        String storeName = (role == Role.COMPANY_WORKER)
                ? user.getCompany().getName()
                : user.getStore().getName();

        // 📝 보고서 생성
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
        File file = new File("/tmp/" + filename);  // 보고서 파일 저장 경로

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