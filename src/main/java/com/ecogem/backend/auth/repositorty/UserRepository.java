package com.ecogem.backend.auth.repositorty;

import com.ecogem.backend.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByStoreId(Long storeId);
}