package com.ecogem.backend.domain.repository;

import com.ecogem.backend.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByCompany_IdAndStore_Id(Long companyId, Long storeId);
    void deleteByCompanyIdAndStoreId(Long companyId, Long storeId);
}

