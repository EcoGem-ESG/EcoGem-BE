package com.ecogem.backend.domain.repository;

import com.ecogem.backend.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    Optional<Company> findByUserId(Long userId);
}
