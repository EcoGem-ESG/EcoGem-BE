package com.ecogem.backend.contract.repository;

import com.ecogem.backend.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    boolean existsByCompanyIdAndStoreId(Long companyId, Long storeId);

    void deleteByCompanyIdAndStoreId(Long companyId, Long storeId);

    List<Contract> findByCompany_Id(Long companyId);

    List<Contract> findByCompany_IdAndStore_NameContainingIgnoreCase(
            Long companyId,
            String keyword
    );
}

