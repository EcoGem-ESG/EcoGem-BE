package com.ecogem.backend.collectionrecord.service;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import com.ecogem.backend.collectionrecord.repository.CollectionRecordRepository;
import com.ecogem.backend.domain.entity.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionRecordService {
    private final CollectionRecordRepository repo;

    public CollectionRecordService(CollectionRecordRepository repo) {
        this.repo = repo;
    }

    public List<CollectionRecordResponseDto> getRecordsForUser(
            Long userId, Role role, LocalDate start, LocalDate end
    ) {
        boolean hasRange = (start != null && end != null);

        List<CollectionRecord> records = switch (role) {
            case STORE_OWNER -> hasRange
                    ? repo.findByStore_UserIdAndCollectedAtBetween(userId, start, end)
                    : repo.findByStore_UserId(userId);
            case COMPANY_WORKER -> hasRange
                    ? repo.findByCompany_UserIdAndCollectedAtBetween(userId, start, end)
                    : repo.findByCompany_UserId(userId);
        };

        return records.stream()
                .map(r -> CollectionRecordResponseDto.builder()
                        .collectedAt(r.getCollectedAt())
                        .collectedBy(r.getCollectedBy())
                        .storeName(r.getStore().getName())
                        .volumeLiter(r.getVolumeLiter())
                        .pricePerLiter(r.getPricePerLiter())
                        .totalPrice(r.getTotalPrice())
                        .build()
                )
                .toList();
    }
}

