package com.ecogem.backend.collectionrecord.repository;

import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {

    // STORE_OWNER 용: store_id 기준 조회
    List<CollectionRecord> findByStore_IdOrderByCollectedAtDesc(Long storeId);
    List<CollectionRecord> findByStore_IdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long storeId, LocalDate start, LocalDate end);

    // COMPANY_WORKER 용: company_id 기준 조회
    List<CollectionRecord> findByCompany_IdOrderByCollectedAtDesc(Long companyId);
    List<CollectionRecord> findByCompany_IdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long companyId, LocalDate start, LocalDate end);

    // 삭제 후 contract 삭제 여부 확인용
    boolean existsByCompany_IdAndStore_Id(Long companyId, Long storeId);
}
