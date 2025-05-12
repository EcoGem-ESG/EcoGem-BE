package com.ecogem.backend.contract.repository;

import com.ecogem.backend.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    // ① 계약 존재 여부 확인 (company_id, store_id)
    boolean existsByCompanyIdAndStoreId(Long companyId, Long storeId);

    // ② 계약 삭제 (company_id, store_id)
    void deleteByCompanyIdAndStoreId(Long companyId, Long storeId);

    // ③ 특정 회사의 모든 계약 조회 (company.id 기준)
    List<Contract> findByCompany_Id(Long companyId);

    // ④ 특정 회사의 계약된 가게 중 이름 검색 (company.id + store.name)
    List<Contract> findByCompany_IdAndStore_NameContainingIgnoreCase(
            Long companyId,
            String keyword
    );
}

