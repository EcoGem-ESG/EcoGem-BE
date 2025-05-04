package com.ecogem.backend.post.service;

import com.ecogem.backend.post.dto.PostResponseDto;
import com.ecogem.backend.post.repository.PostProjection;
import com.ecogem.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;

    /** 반경 필터링 */
    public List<PostResponseDto> listPosts(double lat, double lng, int radiusKm) {
        List<PostProjection> list = postRepo.findWithinRadius(lat, lng, radiusKm);

        return list.stream()
                .map(p -> PostResponseDto.builder()
                        .postId(p.getPostId())
                        .storeName(p.getStoreName())
                        .content(p.getContent())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }


    /** radius 미선택 시: 전체 게시글 최신 작성순 */
    public List<PostResponseDto> listAllPosts() {
        return postRepo.findAllOrdered().stream()
                .map(p -> PostResponseDto.builder()
                        .postId(p.getPostId())
                        .storeName(p.getStoreName())
                        .content(p.getContent())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
