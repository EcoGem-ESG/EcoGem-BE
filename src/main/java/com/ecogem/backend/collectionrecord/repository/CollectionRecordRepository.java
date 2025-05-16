package com.ecogem.backend.collectionrecord.repository;

import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {

    // For STORE_OWNER
    List<CollectionRecord> findByStore_IdOrderByCollectedAtDesc(Long storeId);
    List<CollectionRecord> findByStore_IdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long storeId, LocalDate start, LocalDate end);

    // For COMPANY_WORKER
    List<CollectionRecord> findByCompany_IdOrderByCollectedAtDesc(Long companyId);
    List<CollectionRecord> findByCompany_IdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long companyId, LocalDate start, LocalDate end);

    // Check if a contract exists after deletion
    boolean existsByCompany_IdAndStore_Id(Long companyId, Long storeId);
}
