package com.ecogem.backend.collectionrecord.controller;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.service.CollectionRecordService;
import com.ecogem.backend.domain.entity.Role;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/collection-records")
public class CollectionRecordController {

    private final CollectionRecordService service;

    public CollectionRecordController(CollectionRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCollectionRecords(
            @RequestParam(value = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "end_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            // ▶ 아직 Security 연동 전 테스트용 파라미터
            @RequestParam("user_id") Long userId,
            @RequestParam("role") String roleStr
    ) {
        Role role = Role.valueOf(roleStr.toUpperCase());

        List<CollectionRecordResponseDto> records =
                service.getRecordsForUser(userId, role, startDate, endDate);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "COLLECTION_RECORD_LIST");
        resp.put("records", records);

        return ResponseEntity.ok(resp);
    }
}
