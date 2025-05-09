package com.ecogem.backend.auth.dto;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private Role role;
    private Status status;
}