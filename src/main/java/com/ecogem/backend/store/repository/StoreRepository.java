package com.ecogem.backend.store.repository;

import com.ecogem.backend.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecogem.backend.auth.domain.User;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByUser(User user);

}
