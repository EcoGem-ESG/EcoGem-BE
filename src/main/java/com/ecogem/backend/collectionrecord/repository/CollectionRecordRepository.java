package com.ecogem.backend.collectionrecord.repository;

import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {

    List<CollectionRecord> findByStore_UserId(Long userId);
    List<CollectionRecord> findByStore_UserIdAndCollectedAtBetween(
            Long userId, LocalDate start, LocalDate end
    );

    List<CollectionRecord> findByCompany_UserId(Long userId);
    List<CollectionRecord> findByCompany_UserIdAndCollectedAtBetween(
            Long userId, LocalDate start, LocalDate end
    );
}

