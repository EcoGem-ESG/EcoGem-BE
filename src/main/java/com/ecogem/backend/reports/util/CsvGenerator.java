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

    public String generateCsv(List<CollectionRecordResponseDto> records, String filename) {
        String filePath = "/tmp/" + filename;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("recordId,collectedAt,collectedBy,storeName,volumeLiter,pricePerLiter,totalPrice");


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

            log.info("CSV 파일 생성 완료: {}", filePath);
        } catch (IOException e) {
            log.error("CSV 생성 실패", e);
            throw new RuntimeException("CSV 파일 생성 실패");
        }

        return filePath;
    }
}
