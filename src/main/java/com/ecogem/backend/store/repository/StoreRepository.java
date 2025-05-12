package com.ecogem.backend.store.repository;

import com.ecogem.backend.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByName(String name);
    // userId로 Store를 가져오는 커스텀 쿼리
    // User → Store 의 ManyToOne 맵핑을 타고 올라갑니다.
    @Query("SELECT u.store FROM User u WHERE u.id = :userId")
    Optional<Store> findByUserId(@Param("userId") Long userId);
}
