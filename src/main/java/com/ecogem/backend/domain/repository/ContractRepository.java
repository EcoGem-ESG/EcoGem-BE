package com.ecogem.backend.domain.repository;

import com.ecogem.backend.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByCompanyIdAndStoreId(Long companyId, Long storeId);

    // 회사(company_id) 와 가게(store_id) 로 계약 삭제
    void deleteByCompanyIdAndStoreId(Long companyId, Long storeId);

    // 회사(userId) 가 맺은 계약 목록 조회
    List<Contract> findByCompany_UserId(Long companyUserId);

    // 검색어가 포함된 계약된 가게 조회
    List<Contract> findByCompany_UserIdAndStore_NameContainingIgnoreCase(
            Long companyUserId,
            String keyword
    );

}

