package com.ecogem.backend.company.repository;

import com.ecogem.backend.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT u.company FROM User u WHERE u.id = :userId")
    Optional<Company> findByUserId(@Param("userId") Long userId);

}
