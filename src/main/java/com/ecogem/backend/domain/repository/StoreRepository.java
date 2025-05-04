package com.ecogem.backend.domain.repository;

import com.ecogem.backend.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByName(String name);
}
