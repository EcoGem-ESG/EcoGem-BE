package com.ecogem.backend.contract.controller;

import com.ecogem.backend.contract.dto.AddContractedStoreRequestDto;
import com.ecogem.backend.contract.dto.ContractedStoreResponseDto;
import com.ecogem.backend.contract.service.ContractService;
import com.ecogem.backend.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts/stores")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Live Server 주소
public class ContractController {

    private final ContractService service;

    /**
     * 계약한 가게리스트에서 목록 조회 및 가게 검색
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getContractedStores(
        @RequestParam("user_id") Long userId,
        @RequestParam("role") String roleStr,
        @RequestParam(value = "search", required = false) String search
    ) {
        Role role = Role.valueOf(roleStr.toUpperCase());
        List<ContractedStoreResponseDto> data = service.getContractedStore(userId, role, search);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "CONTRACTED_STORE_LIST");
        resp.put("data", data);

        return ResponseEntity.ok(resp);
    }

    /**
     * 계약힌 가게 리스트에 가게 추가
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addStore(
            @RequestParam("user_id") Long userId,
            @RequestParam("role") String roleStr,
            @RequestBody AddContractedStoreRequestDto dto
    ) {
        Role role = Role.valueOf(roleStr.toUpperCase());
        service.addContractedStore(userId, role, dto);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 201);
        resp.put("message", "CONTRACTED_STORE_REGISTERED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    /**
     * 계약한 가게 리스트에서 가게 삭제
     */
    @DeleteMapping("/{store_id}")
    public ResponseEntity<Map<String,Object>> deleteContractedStore(
            @PathVariable("store_id") Long storeId,
            @RequestParam("user_id") Long userId,
            @RequestParam("role") String roleStr
    ) {
        // Role 변환
        Role role = Role.valueOf(roleStr.toUpperCase());

        // 서비스 호출
        service.deleteContractedStore(userId, role, storeId);

        // 응답 생성
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "CONTRACTED_STORE_DELETE_SUCCESS");

        return ResponseEntity.ok(resp);

    }
}
