package com.ecogem.backend.companies.repository;

import com.ecogem.backend.companies.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
