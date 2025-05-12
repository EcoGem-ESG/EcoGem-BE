package com.ecogem.backend.reports.util;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class CsvGenerator {

    public String generateCsv(List<CollectionRecordResponseDto> records) {
        try {
            // 1) 시스템 임시 디렉토리, 고유한 임시파일 생성
            Path tmpFile = Files.createTempFile(
                    Paths.get(System.getProperty("java.io.tmpdir")),
                    "ecogem_report_",
                    ".csv"
            );

            // 2) try-with-resources 로 자동 close
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(tmpFile))) {
                writer.println("recordId,collectedAt,collectedBy,storeName,volumeLiter,pricePerLiter,totalPrice");
                for (var r : records) {
                    writer.printf(
                            "%d,%s,%s,%s,%.2f,%.2f,%.2f%n",
                            r.getRecordId(),
                            r.getCollectedAt(),
                            r.getCollectedBy(),
                            r.getStoreName(),
                            r.getVolumeLiter(),
                            r.getPricePerLiter(),
                            r.getTotalPrice()
                    );
                }
            }

            log.info("CSV 파일 생성 완료: {}", tmpFile);
            return tmpFile.toString();

        } catch (IOException e) {
            log.error("CSV 생성 실패", e);
            throw new RuntimeException("CSV 파일 생성 실패", e);
        }
    }
}
