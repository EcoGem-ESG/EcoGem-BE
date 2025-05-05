package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDeleteResponseDto {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;
}
