package com.ecogem.backend.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    /** 작성자 유저 ID (User 엔티티 미구현 상태이므로 Long으로 처리) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    /** 부모댓글 → 자식(대댓글) 목록 */
    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 소프트 삭제 플래그 */
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 내용 수정용 */
    public void updateContent(String content) {
        this.content = content;
    }

    /** 소프트 삭제 로직 */
    public void softDelete() {
        this.deleted = true;
        this.content = "[삭제된 댓글]";
    }
}