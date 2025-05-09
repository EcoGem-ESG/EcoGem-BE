package com.ecogem.backend.auth.controller;

import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.dto.LoginRequestDto;
import com.ecogem.backend.auth.dto.SignupRequestDto;
import com.ecogem.backend.auth.service.AuthService;
import com.ecogem.backend.auth.security.JwtProvider; // ✅ 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    // Sign in
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto request) {
        authService.signup(
                request.getLoginId(),
                request.getPwd(),
                request.getEmail(),
                request.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "success", true,
                        "code", 201,
                        "message", "USER_REGISTER_SUCCESS"
                )
        );
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        User user = authService.login(request.getLoginId(), request.getPwd());

        String token = jwtProvider.createToken(user.getId(), user.getRole().name());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user_id", user.getId());
        data.put("role", user.getRole());
        data.put("status", user.getStatus());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "code", 200,
                        "message", "LOGIN_SUCCESS",
                        "data", data
                )
        );
    }
}
