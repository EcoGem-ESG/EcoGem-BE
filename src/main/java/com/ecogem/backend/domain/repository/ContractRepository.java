package com.ecogem.backend.domain.repository;

import com.ecogem.backend.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByCompany_IdAndStore_Id(Long companyId, Long storeId);
    void deleteByCompanyIdAndStoreId(Long companyId, Long storeId);

    // 회사(userId) 가 맺은 계약 목록 조회
    List<Contract> findByCompany_UserId(Long companyUserId);
}

