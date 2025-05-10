package com.ecogem.backend.companies.repository;

import com.ecogem.backend.companies.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecogem.backend.auth.domain.User;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByUser(User user);
}
