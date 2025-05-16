package com.ecogem.backend.reports.util;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
@Slf4j
public class CsvGenerator {

    /**
     * Accepts a list of records and writes them as a CSV file to /tmp/{filename},
     * then returns the absolute file path.
     */
    public String generateCsv(List<CollectionRecordResponseDto> records, String filename) {
        String filePath = "/tmp/" + filename;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            // 1) Write header row
            writer.println("recordId,collectedAt,collectedBy,storeName,volumeLiter,pricePerLiter,totalPrice");

            // 2) Write data rows
            for (CollectionRecordResponseDto record : records) {
                writer.printf("%d,%s,%s,%s,%.2f,%.2f,%.2f%n",
                        record.getRecordId(),
                        record.getCollectedAt(),
                        record.getCollectedBy(),
                        record.getStoreName(),
                        record.getVolumeLiter(),
                        record.getPricePerLiter(),
                        record.getTotalPrice()
                );
            }

            log.info("CSV file generated successfully: {}", filePath);
            return filePath;

        } catch (IOException e) {
            log.error("Failed to generate CSV: {}", filePath, e);
            throw new RuntimeException("Failed to generate CSV file", e);
        }
    }
}
