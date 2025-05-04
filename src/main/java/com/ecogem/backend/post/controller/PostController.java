package com.ecogem.backend.post.controller;

import com.ecogem.backend.post.dto.PostResponseDto;
import com.ecogem.backend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
