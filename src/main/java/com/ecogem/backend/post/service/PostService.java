package com.ecogem.backend.post.service;

import com.ecogem.backend.domain.entity.Store;
import com.ecogem.backend.domain.repository.StoreRepository;
import com.ecogem.backend.post.dto.PostCreateRequestDto;
import com.ecogem.backend.post.dto.PostCreateResponseDto;
import com.ecogem.backend.post.dto.PostResponseDto;
import com.ecogem.backend.post.entity.Post;
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
}
