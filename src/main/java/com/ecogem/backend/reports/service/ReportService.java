package com.ecogem.backend.reports.service;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.service.CollectionRecordService;
import com.ecogem.backend.domain.entity.Role;
import com.ecogem.backend.reports.util.CsvGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CollectionRecordService collectionRecordService;
    private final CsvGenerator csvGenerator;

    public String generateReport(Long userId, Role role, LocalDate start, LocalDate end) {
        // ✅ 1. 수거기록 조회
        List<CollectionRecordResponseDto> records =
                collectionRecordService.getRecordsForUser(userId, role, start, end);

        // ✅ 2. CSV 생성
        String filename = "report_" + LocalDate.now() + ".csv";
        String csvPath = csvGenerator.generateCsv(records, filename);

        // ✅ 3. Python 실행 (CSV → PDF 변환)
        String pdfPath = runPythonScript(csvPath);

        // ✅ 4. 생성된 PDF 파일 경로 반환
        return pdfPath;
    }

    private String runPythonScript(String csvPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("python3", "report_generator.py", csvPath);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputPath = reader.readLine();  // PDF 경로

            int exitCode = process.waitFor();
            if (exitCode != 0 || outputPath == null || outputPath.isEmpty()) {
                throw new RuntimeException("Python 보고서 생성 실패");
            }

            return outputPath;
        } catch (Exception e) {
            throw new RuntimeException("Python 스크립트 실행 중 오류 발생", e);
        }
    }
}
