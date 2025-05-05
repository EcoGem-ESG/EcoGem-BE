package com.ecogem.backend.post.repository;


import com.ecogem.backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 반경 필터링
    @Query(value =
            "SELECT " +
                    "  p.id          AS postId, " +
                    "  s.name        AS storeName, " +
                    "  p.content     AS content, " +
                    "  p.status      AS status, " +
                    "  p.created_at  AS createdAt " +
                    "FROM posts p " +
                    "JOIN stores s ON p.store_id = s.id " +
                    "WHERE ( " +
                    "  6371 * acos( " +
                    "    cos(radians(:lat)) " +
                    "    * cos(radians(s.latitude)) " +
                    "    * cos(radians(s.longitude) - radians(:lng)) " +
                    "    + sin(radians(:lat)) " +
                    "    * sin(radians(s.latitude)) " +
                    "  ) " +
                    ") <= :radius " +
                    "ORDER BY p.created_at DESC",
            nativeQuery = true)
    List<PostProjection> findWithinRadius(
            @Param("lat")    double lat,
            @Param("lng")    double lng,
            @Param("radius") int    radiusKm
    );

    // 전체 게시글 최신 작성순 조회
    @Query(value =
            "SELECT " +
                    "  p.id          AS postId, " +
                    "  s.name        AS storeName, " +
                    "  p.content     AS content, " +
                    "  p.status      AS status, " +
                    "  p.created_at  AS createdAt " +
                    "FROM posts p " +
                    "JOIN stores s ON p.store_id = s.id " +
                    "ORDER BY p.created_at DESC",
            nativeQuery = true)
    List<PostProjection> findAllOrdered();


}

