package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateRequestDto {

    @JsonProperty("post_id")
    @NotNull
    private Long postId;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("content")
    @NotBlank
    private String content;

}