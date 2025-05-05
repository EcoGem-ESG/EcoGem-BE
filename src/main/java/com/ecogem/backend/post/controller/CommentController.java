package com.ecogem.backend.post.controller;

import com.ecogem.backend.post.dto.CommentCreateRequestDto;
import com.ecogem.backend.post.dto.CommentCreateResponseDto;
import com.ecogem.backend.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

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
}
