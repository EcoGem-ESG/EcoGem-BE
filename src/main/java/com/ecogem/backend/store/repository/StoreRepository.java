package com.ecogem.backend.store.repository;

import com.ecogem.backend.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByName(String name);

    @Query("SELECT u.store FROM User u WHERE u.id = :userId")
    Optional<Store> findByUserId(@Param("userId") Long userId);
}
