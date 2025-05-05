package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentUpdateRequestDto {

    /** 수정 요청하는 사용자 ID (작성자 체크용) */
    @JsonProperty("user_id")
    @NotNull
    private Long   userId;

    @JsonProperty("content")
    @NotBlank
    private String content;
}
