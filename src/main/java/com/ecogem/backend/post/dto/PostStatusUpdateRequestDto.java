package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStatusUpdateRequestDto {

    @JsonProperty("status")
    @NotNull
    private String status;
}
