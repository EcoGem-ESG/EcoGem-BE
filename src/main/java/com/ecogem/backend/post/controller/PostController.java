package com.ecogem.backend.post.controller;

import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    /**
     * 게시판에서 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false) Integer radius
    ) {
        List<PostResponseDto> data;

        if (radius == null) {
            // radius 미선택 → 전체 게시글 최신 작성순
            data = postService.listAllPosts();
        } else if (radius == 5 || radius == 10) {
            // 5km 혹은 10km 반경 필터링
            data = postService.listPosts(lat, lng, radius);
        } else {
            // 잘못된 radius
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "code",    400,
                            "message", "INVALID_RADIUS"
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
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long postId) {
        PostDetailResponseDto data = postService.getPostDetail(postId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code", 200,
                "message", "POST_DETAIL_SUCCESS",
                "data", data
        ));
    }

    /**
     * 게시글 작성
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody @Validated PostCreateRequestDto request
    ) {
        PostCreateResponseDto data = postService.createPost(request);

        // java.net.URI 로 Location 변수 선언
        URI location = URI.create("/api/posts/" + data.getPostId());

        return ResponseEntity
                .created(location)  // HTTP 201 + Location 헤더 자동 세팅
                .body(Map.of(
                        "success", true,
                        "code",    201,
                        "message", "POST_CREATE_SUCCESS",
                        "data",    data
                ));
    }


    /**
     * 게시글 상태 변경
     */
    @PatchMapping("/{postId}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long postId,
            @RequestBody @Validated PostStatusUpdateRequestDto request
    ) {
        PostStatusUpdateResponseDto data =
                postService.updateStatus(postId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_STATUS_UPDATED",
                "data",    data
        ));
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestBody @Validated PostUpdateRequestDto request
    ) {
        PostUpdateResponseDto data = postService.updatePost(postId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "code",    200,
                "message", "POST_UPDATE_SUCCESS",
                "data",    data
        ));
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDeleteResponseDto> deletePost(
            @PathVariable Long postId,
            @RequestParam Long storeId
    ) {
        PostDeleteResponseDto response = postService.deletePost(postId, storeId);
        return ResponseEntity.ok(response);
    }

}
