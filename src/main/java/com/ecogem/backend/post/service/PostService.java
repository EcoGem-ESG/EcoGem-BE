package com.ecogem.backend.post.service;

import com.ecogem.backend.auth.repositorty.UserRepository;
import com.ecogem.backend.auth.security.CustomUserDetails;
import com.ecogem.backend.company.domain.Company;
import com.ecogem.backend.company.repository.CompanyRepository;
import com.ecogem.backend.post.dto.*;
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
    private final UserRepository    userRepo;

    /**
     * Filter posts by company location if radiusKm is provided; otherwise return all posts
     */
    public List<PostResponseDto> listPostsByCompany(Long userId, Integer radiusKm) {
        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company found for user_id=" + userId));

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
     * Retrieve all posts ordered by creation time descending
     */
    public List<PostResponseDto> listAllPosts() {
        return postRepo.findAllOrdered().stream()
                .map(p -> PostResponseDto.builder()
                        .postId(p.getPostId())
                        .storeName(p.getStoreName())
                        .content(p.getContent())
                        .status(p.getStatus())
                        .createdAt(p.getCreatedAt())
                        .build())
                .toList();
    }

    /**
     * Retrieve detailed post information including nested comments
     */
    @Transactional(readOnly = true)
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        List<CommentDetailResponseDto> flatComments = commentRepo
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

        Map<Long, CommentDetailResponseDto> commentMap = new LinkedHashMap<>();
        flatComments.forEach(dto -> commentMap.put(dto.getCommentId(), dto));

        List<CommentDetailResponseDto> rootComments = new ArrayList<>();
        for (CommentDetailResponseDto dto : commentMap.values()) {
            if (dto.getParentId() == null) {
                rootComments.add(dto);
            } else {
                CommentDetailResponseDto parent = commentMap.get(dto.getParentId());
                if (parent != null) parent.getChildren().add(dto);
                else rootComments.add(dto);
            }
        }

        Long ownerUserId = userRepo.findByStoreId(post.getStore().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No user found for store_id=" + post.getStore().getId()))
                .getId();

        // Build final DTO
        return PostDetailResponseDto.builder()
                .postId(post.getId())
                .storeId(post.getStore().getId())
                .storeName(post.getStore().getName())
                .userId(ownerUserId)
                .content(post.getContent())
                .status(post.getStatus().name())
                .createdAt(post.getCreatedAt())
                .comments(rootComments)
                .build();
    }

    private String resolveAuthorName(Long userId) {
        return companyRepo.findByUserId(userId)
                .map(Company::getName)
                .orElseGet(() -> storeRepo.findByUserId(userId)
                        .map(Store::getName)
                        .orElse("Unknown"));
    }

    /**
     * Create a new post for the authenticated store owner
     */
    @Transactional
    public PostCreateResponseDto createPost(PostCreateRequestDto dto) {
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
     * Update the status of a post (e.g., ACTIVE, RESERVED, COMPLETED)
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
            throw new IllegalArgumentException("Access denied.");

        PostStatus newStatus = PostStatus.valueOf(dto.getStatus());
        post.setStatus(newStatus);

        return PostStatusUpdateResponseDto.builder()
                .postId(post.getId())
                .newStatus(newStatus.name())
                .build();
    }

    /**
     * Update the content of an existing post
     */
    @Transactional
    public PostUpdateResponseDto updatePost(Long postId, PostUpdateRequestDto dto) {
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId))
            throw new IllegalArgumentException("Access denied.");

        post.setContent(dto.getContent());
        return PostUpdateResponseDto.builder()
                .postId(post.getId())
                .build();
    }

    /**
     * Delete a post by the store owner
     */
    @Transactional
    public PostDeleteResponseDto deletePost(Long postId) {
        Long storeId = ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).getUser().getStore().getId();

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        if (!post.getStore().getId().equals(storeId))
            throw new IllegalArgumentException("Access denied.");

        postRepo.delete(post);
        return PostDeleteResponseDto.builder()
                .success(true)
                .code(200)
                .message("POST_DELETE_SUCCESS")
                .build();
    }
}
