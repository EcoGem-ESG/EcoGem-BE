package com.ecogem.backend.auth.dto;


import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String loginId;
    private String pwd;
}