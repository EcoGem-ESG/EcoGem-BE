package com.ecogem.backend.post.controller;

import com.ecogem.backend.post.dto.PostCreateRequestDto;
import com.ecogem.backend.post.dto.PostCreateResponseDto;
import com.ecogem.backend.post.dto.PostResponseDto;
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

}
