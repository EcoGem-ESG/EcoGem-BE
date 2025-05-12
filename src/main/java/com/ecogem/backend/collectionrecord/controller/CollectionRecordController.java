package com.ecogem.backend.collectionrecord.controller;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordRequestDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordUpdateDto;
import com.ecogem.backend.collectionrecord.service.CollectionRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collection-records")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })
public class CollectionRecordController {

    private final CollectionRecordService service;

    public CollectionRecordController(CollectionRecordService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCollectionRecords(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(value = "start_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "end_date",   required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate   endDate
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        List<CollectionRecordResponseDto> records =
                service.getRecordsForUser(userId, role, startDate, endDate);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    200);
        resp.put("message", "COLLECTION_RECORD_LIST");
        resp.put("records", records);

        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerCollectionRecord(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody CollectionRecordRequestDto dto
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        service.registerRecord(userId, role, dto);

        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    201);
        resp.put("message", "RECORD_REGISTER_SUCCESS");

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PatchMapping("/{record_id}")
    public ResponseEntity<Map<String, Object>> updateCollectionRecord(
            @AuthenticationPrincipal    CustomUserDetails principal,
            @PathVariable("record_id")  Long    recordId,
            @RequestBody                CollectionRecordUpdateDto dto
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        service.updateRecord(userId, role, recordId, dto);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    200);
        resp.put("message", "RECORD_UPDATE_SUCCESS");
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{record_id}")
    public ResponseEntity<Map<String, Object>> deleteCollectionRecord(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("record_id") Long recordId
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        service.deleteRecord(userId, role, recordId);

        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    200);
        resp.put("message", "RECORD_DELETE_SUCCESS");
        return ResponseEntity.ok(resp);
    }
}
