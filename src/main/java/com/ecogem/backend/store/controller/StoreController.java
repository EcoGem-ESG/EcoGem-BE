package com.ecogem.backend.store.controller;

import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.store.dto.StoreRequestDto;
import com.ecogem.backend.store.dto.StoreResponseDto;
import com.ecogem.backend.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponseDto> registerStore(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody StoreRequestDto requestDto
    ) {
        Long userId = principal.getUser().getId();
        StoreResponseDto response = storeService.registerStore(userId, requestDto);
        return ResponseEntity.ok(response);
    }
}
