package com.ecogem.backend.contract.controller;

import com.ecogem.backend.contract.dto.ContractedStoreDto;
import com.ecogem.backend.contract.service.ContractService;
import com.ecogem.backend.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        @RequestParam("role") String roleStr
    ) {
        Role role = Role.valueOf(roleStr.toUpperCase());
        List<ContractedStoreDto> data = service.getContractedStore(userId, role);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "CONTRACTED_STORE_LIST");
        resp.put("data", data);

        return ResponseEntity.ok(resp);
    }
}
