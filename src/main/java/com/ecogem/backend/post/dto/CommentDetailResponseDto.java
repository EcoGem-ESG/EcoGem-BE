package com.ecogem.backend.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentDetailResponseDto {

    @JsonProperty("comment_id")
    private Long commentId;

    // TODO: user_name 구현되면 바꾸기
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("author_name")
    private String authorName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /** 소프트 삭제 여부 */
    @JsonProperty("deleted")
    private boolean deleted;

    /** 대댓글(자식) 리스트 */
    @JsonProperty("children")
    @Builder.Default
    private List<CommentDetailResponseDto> children = new ArrayList<>();
}
