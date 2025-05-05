package com.ecogem.backend.post.repository;

import com.ecogem.backend.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 댓글/대댓글을 작성순(created_at 오름차순)으로 조회 */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 댓글 + 대댓글 포함, 특정 게시글의 모든 댓글 조회
    List<Comment> findByPostId(Long postId);
}
