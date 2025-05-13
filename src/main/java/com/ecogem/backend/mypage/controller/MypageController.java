package com.ecogem.backend.mypage.controller;

import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.mypage.dto.MypageUpdateRequest;
import com.ecogem.backend.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMypage(@AuthenticationPrincipal User user) {
        Object data = mypageService.getMypage(user);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "MYPAGE_SUCCESS");
        resp.put("data", data);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> updateMypage(@AuthenticationPrincipal User user,
                                                            @RequestBody MypageUpdateRequest request) {
        mypageService.updateMypage(user, request);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("code", 200);
        resp.put("message", "MY_PAGE_UPDATE_SUCCESS");
        return ResponseEntity.ok(resp);
    }

}