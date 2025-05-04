package com.ecogem.backend.collectionrecord.repository;

import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {

    // STORE_OWNER 용 (최신순)
    List<CollectionRecord> findByStore_UserIdOrderByCollectedAtDesc(Long userId);
    List<CollectionRecord> findByStore_UserIdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long userId, LocalDate start, LocalDate end
    );

    // COMPANY_WORKER 용 (최신순)
    List<CollectionRecord> findByCompany_UserIdOrderByCollectedAtDesc(Long userId);
    List<CollectionRecord> findByCompany_UserIdAndCollectedAtBetweenOrderByCollectedAtDesc(
            Long userId, LocalDate start, LocalDate end
    );
}

