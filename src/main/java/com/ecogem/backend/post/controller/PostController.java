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
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" })   // Live Server 주소
public class PostController {

    private final PostService postService;

    /** 게시판에서 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<?> getPosts(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(value = "radius", required = false) Integer radiusKm
    ) {
        Long userId = principal.getUser().getId();
        String role = principal.getUser().getRole().name();

        List<PostResponseDto> data;
        if ("COMPANY_WORKER".equals(role)) {
            // 회사: radiusKm 있으면 필터, 없으면 전체
            data = postService.listPostsByCompany(userId, radiusKm);
        } else if ("STORE_OWNER".equals(role)) {
            // 가게 주인: 반경 무시, 전체 최신순
            data = postService.listAllPosts();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "success", false,
                            "code",    403,
                            "message", "권한이 없습니다."
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_LIST",
                "data",    data
        ));
    }

    /** 게시글 상세 조회 */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long postId) {
        var data = postService.getPostDetail(postId);
        return ResponseEntity.ok(Map.of(
                "success", true, "code", 200,
                "message", "POST_DETAIL_SUCCESS", "data", data
        ));
    }

    /** 게시글 작성 */
    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Validated PostCreateRequestDto req
    ) {
        var data = postService.createPost(req);
        URI location = URI.create("/api/posts/" + data.getPostId());
        return ResponseEntity.created(location).body(Map.of(
                "success", true, "code", 201,
                "message", "POST_CREATE_SUCCESS", "data", data
        ));
    }

    /** 게시글 상태 변경 */
    @PatchMapping("/{postId}/status")
    public ResponseEntity<?> changeStatus(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @RequestBody @Validated PostStatusUpdateRequestDto req
    ) {
        var data = postService.updateStatus(postId, req);
        return ResponseEntity.ok(Map.of(
                "success", true, "code", 200,
                "message", "POST_STATUS_UPDATED", "data", data
        ));
    }

    /** 게시글 수정 */
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequestDto req
    ) {
        var data = postService.updatePost(postId, req);
        return ResponseEntity.ok(Map.of(
                "success", true, "code", 200,
                "message", "POST_UPDATE_SUCCESS", "data", data
        ));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        var data = postService.deletePost(postId);
        return ResponseEntity.ok(Map.of(
                "success", true, "code", 200,
                "message", "POST_DELETE_SUCCESS", "data", data
        ));
    }
}
