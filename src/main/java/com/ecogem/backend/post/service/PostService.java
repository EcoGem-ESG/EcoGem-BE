package com.ecogem.backend.post.service;

import com.ecogem.backend.domain.entity.Store;
import com.ecogem.backend.domain.repository.StoreRepository;
import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.entity.Post;
import com.ecogem.backend.post.entity.PostStatus;
import com.ecogem.backend.post.repository.PostProjection;
import com.ecogem.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final StoreRepository storeRepo;

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

    /**
     * 게시글 작성
     * 테스트용으로 storeId를 직접 받음
     */
    @Transactional
    public PostCreateResponseDto createPost(PostCreateRequestDto dto) {
        Store store = storeRepo.findById(dto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + dto.getStoreId()));

        Post post = Post.builder()
                .store(store)
                .content(dto.getContent())
                // status 는 엔티티에서 @Builder.Default 로 ACTIVE 로 초기화됨
                .build();

        Post saved = postRepo.save(post);

        return PostCreateResponseDto.builder()
                .postId(saved.getId())
                .build();

    }

    /**
     * 게시글 상태 변경
     */
    @Transactional
    public PostStatusUpdateResponseDto updateStatus(
            Long postId,
            PostStatusUpdateRequestDto dto
    ) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 문자열 → enum 변환, 잘못된 값이면 IllegalArgumentException 발생
        PostStatus newStatus = PostStatus.valueOf(dto.getStatus());

        post.setStatus(newStatus);  // 엔티티 상태 변경

        // @Transactional 이므로 flush 시점에 UPDATE 쿼리 실행
        // 또는 명시적 save 호출 가능: postRepo.save(post);

        return PostStatusUpdateResponseDto.builder()
                .postId(post.getId())
                .newStatus(newStatus.name())
                .build();
    }


    /**
     * 게시글 내용(content) 수정
     */
    @Transactional
    public PostUpdateResponseDto updatePost(Long postId, PostUpdateRequestDto dto) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        post.setContent(dto.getContent());

        return PostUpdateResponseDto.builder()
                .postId(post.getId())
                .build();
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public PostDeleteResponseDto deletePost(Long postId) {
        postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        postRepo.deleteById(postId);

        return PostDeleteResponseDto.builder()
                .success(true)
                .code(200)
                .message("POST_DELETE_SUCCESS")
                .build();
    }
}
