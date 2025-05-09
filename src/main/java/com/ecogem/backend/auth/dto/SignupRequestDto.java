package com.ecogem.backend.auth.dto;

import com.ecogem.backend.auth.domain.Role;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String loginId;
    private String pwd;
    private String email;
    private Role role;
}