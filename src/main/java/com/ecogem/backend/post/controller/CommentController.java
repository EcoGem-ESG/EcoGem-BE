package com.ecogem.backend.post.controller;

import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Live Server 주소
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글/대댓글 작성
     */
    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody @Validated CommentCreateRequestDto request
    ) {
        CommentCreateResponseDto data =
                commentService.createComment(request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code", 200,
                "message", "COMMENT_CREATE_SUCCESS",
                "data", data
        ));
    }

    /**
     * 댓글/대댓글 내용 수정
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequestDto request
    ) {
        CommentUpdateResponseDto data =
                commentService.updateComment(commentId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "COMMENT_UPDATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * 댓글/대댓글 소프트 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @RequestParam("user_id") Long userId   // 테스트용으로 쿼리 파라미터로 받음
    ) {
        CommentDeleteResponseDto data =
                commentService.deleteComment(commentId, userId);

        return ResponseEntity.ok(data);
    }
}
