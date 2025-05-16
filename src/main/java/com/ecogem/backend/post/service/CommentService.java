package com.ecogem.backend.post.service;

import com.ecogem.backend.post.dto.*;
import com.ecogem.backend.post.entity.Comment;
import com.ecogem.backend.post.entity.Post;
import com.ecogem.backend.post.repository.CommentRepository;
import com.ecogem.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository    postRepo;

    /**
     * Create a new comment or reply
     */
    @Transactional
    public CommentCreateResponseDto createComment(
            CommentCreateRequestDto req,
            Long userId
    ) {
        // 1) Retrieve the post
        Post post = postRepo.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + req.getPostId()));

        // 2) Retrieve parent comment (for replies only)
        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepo.findById(req.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found: " + req.getParentId()));
        }

        // 3) Create comment entity
        Comment comment = Comment.builder()
                .post(post)
                .parent(parent)
                .userId(userId)
                .content(req.getContent())
                .build();

        // 4) Save to repository
        Comment saved = commentRepo.save(comment);

        // 5) Build response DTO
        return CommentCreateResponseDto.builder()
                .commentId(saved.getId())
                .build();
    }

    /**
     * Update content of a comment or reply
     */
    @Transactional
    public CommentUpdateResponseDto updateComment(
            Long commentId,
            CommentUpdateRequestDto req,
            Long userId
    ) {
        // 1) Retrieve the comment
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        // 2) Allow update only by the author
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied.");
        }

        // 3) Update the content
        comment.updateContent(req.getContent());

        // 4) Build response DTO
        return CommentUpdateResponseDto.builder()
                .commentId(comment.getId())
                .build();
    }

    /**
     * Soft-delete a comment or reply
     */
    @Transactional
    public CommentDeleteResponseDto deleteComment(Long commentId, Long userId) {
        // 1) Retrieve the comment
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        // 2) Verify author
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Access denied.");
        }

        // 3) Soft delete the comment
        comment.softDelete();

        // 4) Build response DTO
        return CommentDeleteResponseDto.builder()
                .success(true)
                .code(200)
                .message("COMMENT_DELETE_SUCCESS")
                .build();
    }
}
