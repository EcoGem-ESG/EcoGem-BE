package com.ecogem.backend.companies.repository;

import com.ecogem.backend.companies.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    // ① userId로 Company 조회
    @Query("SELECT u.company FROM User u WHERE u.id = :userId")
    Optional<Company> findByUserId(@Param("userId") Long userId);

}
