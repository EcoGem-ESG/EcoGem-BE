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
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    @GetMapping("/stores")
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
     * 리스트에 가게 추가
     */
    @PostMapping("/stores")
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
}
