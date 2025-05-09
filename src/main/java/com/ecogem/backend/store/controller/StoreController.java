package com.ecogem.backend.store.controller;

import com.ecogem.backend.store.service.StoreService;
import com.ecogem.backend.store.dto.StoreRequestDto;
import com.ecogem.backend.store.dto.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/{userId}")
    public ResponseEntity<StoreResponseDto> registerStore(
            @PathVariable Long userId,
            @RequestBody StoreRequestDto requestDto
    ) {
        StoreResponseDto response = storeService.registerStore(userId, requestDto);
        return ResponseEntity.ok(response);
    }
}
