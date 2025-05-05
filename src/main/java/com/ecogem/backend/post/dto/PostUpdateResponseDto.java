package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateResponseDto {

    @JsonProperty("post_id")
    private Long postId;
}
