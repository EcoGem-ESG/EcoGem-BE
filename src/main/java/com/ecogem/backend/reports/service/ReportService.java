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

        // 1. List all collection
        List<CollectionRecordResponseDto> allRecords =
                collectionRecordService.getRecordsForUser(userId, role, start, end);

        // 2. By the name of stores

        List<CollectionRecordResponseDto> filtered = allRecords.stream()
                .filter(r -> storeName.equalsIgnoreCase(r.getStoreName()))
                .toList();

        if (filtered.isEmpty()) {
            throw new RuntimeException("No collection Record.");
        }


        String filename = "report_" + System.currentTimeMillis() + ".csv";
        String csvPath = csvGenerator.generateCsv(filtered, filename);

        // 4. python run (CSV + storeName + Date)
        return runPythonScript(csvPath, storeName, start.toString(), end.toString());
    }

    private String runPythonScript(String csvPath, String storeName, String startDate, String endDate) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "python3", "report_generator.py", csvPath, storeName, startDate, endDate
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();


            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputPath = reader.readLine();  // PDF route

            int exitCode = process.waitFor();
            if (exitCode != 0 || outputPath == null || outputPath.isEmpty()) {
                throw new RuntimeException("Python report failed");
            }

            // stderr
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = err.readLine()) != null) {
                log.error("Python STDERR: {}", line);
            }
            return outputPath.trim();
        } catch (Exception e) {
            throw new RuntimeException("Python Script Error", e);

        }
    }
}
