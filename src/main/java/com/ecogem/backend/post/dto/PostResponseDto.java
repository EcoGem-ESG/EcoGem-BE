package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponseDto {

    @JsonProperty("post_id")
    private final Long postId;

    @JsonProperty("store_name")
    private final String storeName;

    @JsonProperty("content")
    private final String content;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
}
