package com.ecogem.backend.post.controller;

import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Live Server URLs
public class CommentController {

    private final CommentService commentService;

    /**
     * Create a new comment or reply
     */
    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Validated CommentCreateRequestDto req
    ) {
        Long userId = principal.getUser().getId();
        var data = commentService.createComment(req, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "COMMENT_CREATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * Update an existing comment or reply
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long commentId,
            @RequestBody @Validated CommentUpdateRequestDto req
    ) {
        Long userId = principal.getUser().getId();
        var data   = commentService.updateComment(commentId, req, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "COMMENT_UPDATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * Soft-delete a comment or reply
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long commentId
    ) {
        Long userId = principal.getUser().getId();
        var data   = commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "COMMENT_DELETE_SUCCESS",
                "data",    data
        ));
    }
}
