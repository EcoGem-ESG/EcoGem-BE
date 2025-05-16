package com.ecogem.backend.contract.controller;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.contract.dto.AddContractedStoreRequestDto;
import com.ecogem.backend.contract.dto.ContractedStoreResponseDto;
import com.ecogem.backend.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts/stores")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Live Server URLs
public class ContractController {

    private final ContractService service;

    /**
     * Retrieve the list of contracted stores
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getContractedStores(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(value = "search", required = false) String search
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        List<ContractedStoreResponseDto> data =
                service.getContractedStore(userId, role, search);

        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    200);
        resp.put("message", "CONTRACTED_STORE_LIST");
        resp.put("data",    data);
        return ResponseEntity.ok(resp);
    }

    /**
     * Add a store to the contracted store list
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addStore(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody AddContractedStoreRequestDto dto
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        service.addContractedStore(userId, role, dto);

        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    201);
        resp.put("message", "CONTRACTED_STORE_REGISTERED");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /**
     * Delete a store from the contracted store list
     */
    @DeleteMapping("/{store_id}")
    public ResponseEntity<Map<String,Object>> deleteContractedStore(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("store_id") Long storeId
    ) {
        Long userId = principal.getUser().getId();
        Role role   = principal.getUser().getRole();

        service.deleteContractedStore(userId, role, storeId);

        Map<String,Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code",    200);
        resp.put("message", "CONTRACTED_STORE_DELETE_SUCCESS");
        return ResponseEntity.ok(resp);
    }
}