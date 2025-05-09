package com.ecogem.backend.post.service;

import com.ecogem.backend.domain.entity.Company;
import com.ecogem.backend.domain.entity.Store;
import com.ecogem.backend.domain.repository.CompanyRepository;
import com.ecogem.backend.domain.repository.StoreRepository;
import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.entity.Comment;
import com.ecogem.backend.post.entity.Post;
import com.ecogem.backend.post.entity.PostStatus;
import com.ecogem.backend.post.repository.CommentRepository;
import com.ecogem.backend.post.repository.PostProjection;
import com.ecogem.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final StoreRepository storeRepo;
    private final CommentRepository commentRepo;
    private final CompanyRepository companyRepo;

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
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostDetail(Long postId) {
        // 1) Post 조회
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // 2) 댓글(flat) 작성순 조회 + DTO 변환
        List<CommentDetailResponseDto> flat = commentRepo
                .findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(c -> CommentDetailResponseDto.builder()
                        .commentId(c.getId())
                        .userId(c.getUserId())
                        .authorName(resolveAuthorName(c.getUserId()))
                        .content(c.getContent())
                        .parentId(c.getParent() != null ? c.getParent().getId() : null)
                        .createdAt(c.getCreatedAt())
                        .deleted(c.isDeleted())
                        .build()
                )
                .collect(Collectors.toList());

        // 3) ID→DTO 매핑
        Map<Long, CommentDetailResponseDto> map = new LinkedHashMap<>();
        flat.forEach(dto -> map.put(dto.getCommentId(), dto));

        // 4) 트리 구조로 묶기
        List<CommentDetailResponseDto> roots = new ArrayList<>();
        for (CommentDetailResponseDto dto : map.values()) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                CommentDetailResponseDto parentDto = map.get(dto.getParentId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                } else {
                    // 혹시 부모가 없는 경우엔 최상위로 올리거나 무시
                    roots.add(dto);
                }
            }
        }

        // 5) 최종 DTO 조립 (children 가진 roots 리스트 사용)
        return PostDetailResponseDto.builder()
                .postId(post.getId())
                .storeId(post.getStore().getId())
                .storeName(post.getStore().getName())
                .content(post.getContent())
                .status(post.getStatus().name())
                .createdAt(post.getCreatedAt())
                .comments(roots)
                .build();
    }

    /** userId 에 해당하는 회사명/가게명을 반환 */
    private String resolveAuthorName(Long userId) {
        // 회사(userId) 조회 시 존재하면 회사명
        return companyRepo.findByUserId(userId)
                .map(Company::getName)
                .orElseGet(() -> {
                    // 없으면 가게(userId) 조회 - StoreRepository 에 findByUserId 메서드가 필요합니다
                    return storeRepo.findByUserId(userId)
                            .map(Store::getName)
                            .orElse("알 수 없음");
                });
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

        // 권한 체크: 요청한 storeId 와 실제 post.store.id 가 같아야
        if (!post.getStore().getId().equals(dto.getStoreId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

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

        if (!post.getStore().getId().equals(dto.getStoreId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        post.setContent(dto.getContent());

        return PostUpdateResponseDto.builder()
                .postId(post.getId())
                .build();
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public PostDeleteResponseDto deletePost(Long postId, Long storeId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        postRepo.delete(post); // 댓글과 대댓글은 자동으로 삭제됨

        return PostDeleteResponseDto.builder()
                .success(true)
                .code(200)
                .message("POST_DELETE_SUCCESS")
                .build();
    }


}
