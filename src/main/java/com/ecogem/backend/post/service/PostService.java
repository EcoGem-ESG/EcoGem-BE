package com.ecogem.backend.post.service;

import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.companies.domain.Company;
import com.ecogem.backend.companies.repository.CompanyRepository;
import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.entity.Comment;
import com.ecogem.backend.post.entity.Post;
import com.ecogem.backend.post.entity.PostStatus;
import com.ecogem.backend.post.repository.CommentRepository;
import com.ecogem.backend.post.repository.PostProjection;
import com.ecogem.backend.post.repository.PostRepository;
import com.ecogem.backend.store.domain.Store;
import com.ecogem.backend.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository    postRepo;
    private final StoreRepository   storeRepo;
    private final CommentRepository commentRepo;
    private final CompanyRepository companyRepo;

    /** radiusKm 있을 때 회사 위치기준 필터, 없으면 전체 */
    public List<PostResponseDto> listPostsByCompany(Long userId, Integer radiusKm) {
        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        List<PostProjection> list = (radiusKm != null)
                ? postRepo.findWithinRadius(company.getLatitude(), company.getLongitude(), radiusKm)
                : postRepo.findAllOrdered();

        return list.stream().map(p ->
                PostResponseDto.builder()
                        .postId(p.getPostId())
                        .storeName(p.getStoreName())
                        .content(p.getContent())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .build()
        ).toList();
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

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
                        .build())
                .toList();

        Map<Long, CommentDetailResponseDto> map = new LinkedHashMap<>();
        flat.forEach(dto -> map.put(dto.getCommentId(), dto));

        List<CommentDetailResponseDto> roots = new ArrayList<>();
        for (var dto : map.values()) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                var parent = map.get(dto.getParentId());
                if (parent != null) parent.getChildren().add(dto);
                else roots.add(dto);
            }
        }

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

    private String resolveAuthorName(Long userId) {
        return companyRepo.findByUserId(userId)
                .map(Company::getName)
                .orElseGet(() -> storeRepo.findByUserId(userId)
                        .map(Store::getName)
                        .orElse("알 수 없음"));
    }

    /**
     * 게시글 작성
     */
    @Transactional
    public PostCreateResponseDto createPost(PostCreateRequestDto dto) {
        // storeId는 로그인된 STORE_OWNER의 정보에서 꺼냄
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Store store = storeRepo.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + storeId));

        Post saved = postRepo.save(Post.builder()
                .store(store)
                .content(dto.getContent())
                .build()
        );

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
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId))
            throw new IllegalArgumentException("권한이 없습니다.");

        PostStatus newStatus = PostStatus.valueOf(dto.getStatus());
        post.setStatus(newStatus);

        return PostStatusUpdateResponseDto.builder()
                .postId(post.getId())
                .newStatus(newStatus.name())
                .build();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostUpdateResponseDto updatePost(Long postId, PostUpdateRequestDto dto) {
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId))
            throw new IllegalArgumentException("권한이 없습니다.");

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
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId))
            throw new IllegalArgumentException("권한이 없습니다.");

        postRepo.delete(post);
        return PostDeleteResponseDto.builder()
                .success(true)
                .code(200)
                .message("POST_DELETE_SUCCESS")
                .build();
    }
}
