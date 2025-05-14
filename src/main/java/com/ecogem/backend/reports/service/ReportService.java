package com.ecogem.backend.reports.service;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.service.CollectionRecordService;
import com.ecogem.backend.reports.util.CsvGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final CollectionRecordService collectionRecordService;
    private final CsvGenerator csvGenerator;
    public String generateReport(Long userId, Role role, String storeName, LocalDate start, LocalDate end) {

        List<CollectionRecordResponseDto> allRecords =
                collectionRecordService.getRecordsForUser(userId, role, start, end);


        List<CollectionRecordResponseDto> filtered = allRecords.stream()
                .filter(r -> storeName.equalsIgnoreCase(r.getStoreName()))
                .toList();

        if (filtered.isEmpty()) {
            throw new RuntimeException("수거 기록이 존재하지 않습니다.");
        }


        String filename = "report_" + System.currentTimeMillis() + ".csv";
        String csvPath = csvGenerator.generateCsv(filtered, filename);

        // 4. 파이썬 실행 (CSV + storeName + 날짜)
        return runPythonScript(csvPath, storeName, start.toString(), end.toString());
    }

    private String runPythonScript(String csvPath, String storeName, String startDate, String endDate) {
        try {

            ProcessBuilder builder = new ProcessBuilder(
                    "python3", "report_generator.py", csvPath, storeName, startDate, endDate
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();


            // Windows: "py -3", mac/linux: "python3"
            ProcessBuilder pb = new ProcessBuilder(
                    "py", "-3",
                    script,
                    csvPath,
                    storeName,
                    startDate,
                    endDate
            );
            pb.directory(new File(cwd));
            Process p = pb.start();

            // stdout
            BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String stdout = out.readLine();
            log.info("Python STDOUT: {}", stdout);

            // stderr
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = err.readLine()) != null) {
                log.error("Python STDERR: {}", line);
            }
            return outputPath.trim();
        } catch (Exception e) {
            throw new RuntimeException("Python 스크립트 실행 중 오류 발생", e);

        }
    }
}
