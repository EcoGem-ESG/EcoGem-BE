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

    /**
     * 사용자·역할·기간·가게명을 받아서 CSV → PDF 보고서를 생성하고,
     * Python 스크립트가 출력한 PDF 경로를 리턴합니다.
     */
    public String generateReport(
            Long userId,
            Role role,
            String storeName,
            LocalDate start,
            LocalDate end
    ) {
        // 1) 전체 수거기록 조회
        List<CollectionRecordResponseDto> all = collectionRecordService
                .getRecordsForUser(userId, role, start, end);

        // 2) 특정 가게만 필터링
        List<CollectionRecordResponseDto> filtered = all.stream()
                .filter(r -> storeName.equalsIgnoreCase(r.getStoreName()))
                .collect(Collectors.toList());

        // 3) CSV 생성 (임시파일)
        String csvPath = csvGenerator.generateCsv(filtered);
        log.info("CSV generated: {}", csvPath);

        // 4) Python 스크립트 실행 (CSV → PDF)
        String pdfPath = runPythonScript(csvPath,
                storeName,
                start.toString(),
                end.toString());

        // 5) 임시 CSV 삭제(Optional)
        try {
            Files.deleteIfExists(new File(csvPath).toPath());
            log.info("Temporary CSV deleted: {}", csvPath);
        } catch (Exception e) {
            log.warn("Failed to delete temp CSV: {}", csvPath, e);
        }

        return pdfPath;
    }

    /**
     * @param csvPath     생성된 CSV 파일 경로
     * @param storeName   보고서 대상 가게명 (파이썬 인자)
     * @param startDate   파이썬 인자로 넘길 시작일자 (YYYY-MM-DD)
     * @param endDate     파이썬 인자로 넘길 종료일자 (YYYY-MM-DD)
     */
    private String runPythonScript(
            String csvPath,
            String storeName,
            String startDate,
            String endDate
    ) {
        try {
            String cwd = System.getProperty("user.dir");
            log.info("runPythonScript - cwd: {}", cwd);

            String script = cwd + File.separator + "report_generator.py";
            log.info("runPythonScript - script: {}", script);

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

            int code = p.waitFor();
            log.info("Python exit code: {}", code);

            if (code != 0 || stdout == null || stdout.isBlank()) {
                throw new RuntimeException("Python report generation failed (exit=" + code + ")");
            }
            return stdout.trim();

        } catch (Exception ex) {
            log.error("runPythonScript exception", ex);
            throw new RuntimeException("Error running Python script", ex);
        }
    }
}
