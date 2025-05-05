package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequestDto {

    /** 테스트용으로 store_id 를 직접 전달받습니다 */
    @JsonProperty("store_id")
    @NotNull
    private Long   storeId;

    @JsonProperty("content")
    @NotBlank
    private String content;
}
