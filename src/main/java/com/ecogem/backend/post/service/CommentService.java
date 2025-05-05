package com.ecogem.backend.post.service;

import com.ecogem.backend.post.dto.CommentCreateRequestDto;
import com.ecogem.backend.post.dto.CommentCreateResponseDto;
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

    @Transactional
    public CommentCreateResponseDto createComment(CommentCreateRequestDto req) {
        // 1) Post 조회
        Post post = postRepo.findById(req.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + req.getPostId()));

        // 2) parent 댓글 조회 (대댓글일 때만)
        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepo.findById(req.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found: " + req.getParentId()));
        }

        // 3) 엔티티 생성
        Comment comment = Comment.builder()
                .post(post)
                .parent(parent) // 대댓글이면 parentId→Comment 객체로 조회 후 대입
                .userId(req.getUserId())
                .content(req.getContent())
                .build();

        // 4) 저장
        Comment saved = commentRepo.save(comment);

        // 5) 응답
        return CommentCreateResponseDto.builder()
                .commentId(saved.getId())
                .build();
    }
}
