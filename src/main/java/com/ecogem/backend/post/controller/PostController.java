package com.ecogem.backend.post.controller;

import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Allowed live server origins
public class PostController {

    private final PostService postService;

    /**
     * Retrieve list of posts for the board
     */
    @GetMapping
    public ResponseEntity<?> getPosts(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(value = "radius", required = false) Integer radiusKm
    ) {
        Long userId = principal.getUser().getId();
        String role = principal.getUser().getRole().name();

        List<PostResponseDto> data;
        if ("COMPANY_WORKER".equals(role)) {
            // Company worker: filter by radius if provided
            data = postService.listPostsByCompany(userId, radiusKm);
        } else if ("STORE_OWNER".equals(role)) {
            // Store owner: list all posts regardless of radius
            data = postService.listAllPosts();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "success", false,
                            "code",    403,
                            "message", "Access denied."
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_LIST",
                "data",    data
        ));
    }

    /**
     * Retrieve post details by ID
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long postId) {
        var data = postService.getPostDetail(postId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_DETAIL_SUCCESS",
                "data",    data
        ));
    }

    /**
     * Create a new post
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Validated PostCreateRequestDto req
    ) {
        var data = postService.createPost(req);
        URI location = URI.create("/api/posts/" + data.getPostId());
        return ResponseEntity.created(location).body(Map.of(
                "success", true,
                "code",    201,
                "message", "POST_CREATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * Update the status of an existing post
     */
    @PatchMapping("/{postId}/status")
    public ResponseEntity<?> changeStatus(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @RequestBody @Validated PostStatusUpdateRequestDto req
    ) {
        var data = postService.updateStatus(postId, req);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_STATUS_UPDATED",
                "data",    data
        ));
    }

    /**
     * Update an existing post's content
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequestDto req
    ) {
        var data = postService.updatePost(postId, req);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_UPDATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * Delete a post by ID
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        var data = postService.deletePost(postId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_DELETE_SUCCESS",
                "data",    data
        ));
    }
}
