package com.ecogem.backend.store.repository;

import com.ecogem.backend.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
