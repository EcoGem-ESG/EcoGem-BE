package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateResponseDto {

    @JsonProperty("comment_id")
    private Long commentId;
}