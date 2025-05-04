package com.ecogem.backend.post.repository;

import java.time.LocalDateTime;

public interface PostProjection {
    Long getPostId();

    String getStoreName();

    String getContent();

    String getStatus();

    LocalDateTime getCreatedAt();
}
