package com.ecogem.backend.collectionrecord.service;

import com.ecogem.backend.collectionrecord.dto.CollectionRecordRequestDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordUpdateDto;
import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import com.ecogem.backend.collectionrecord.repository.CollectionRecordRepository;
import com.ecogem.backend.domain.entity.Company;
import com.ecogem.backend.domain.entity.Role;
import com.ecogem.backend.domain.entity.Store;
import com.ecogem.backend.domain.repository.CompanyRepository;
import com.ecogem.backend.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionRecordService {

    private final CollectionRecordRepository recordRepo;
    private final StoreRepository         storeRepo;
    private final CompanyRepository       companyRepo;

    /**
     * 1) 수거기록 조회
     */
    public List<CollectionRecordResponseDto> getRecordsForUser(
            Long userId,
            Role role,
            LocalDate start,
            LocalDate end
    ) {
        boolean hasRange = (start != null && end != null);

        List<CollectionRecord> records = switch (role) {
            case STORE_OWNER -> hasRange
                    ? recordRepo.findByStore_UserIdAndCollectedAtBetweenOrderByCollectedAtDesc(userId, start, end)
                    : recordRepo.findByStore_UserIdOrderByCollectedAtDesc(userId);

            case COMPANY_WORKER -> hasRange
                    ? recordRepo.findByCompany_UserIdAndCollectedAtBetweenOrderByCollectedAtDesc(userId, start, end)
                    : recordRepo.findByCompany_UserIdOrderByCollectedAtDesc(userId);
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

    /**
     * 2) 수거기록 등록
     */
    @Transactional
    public void registerRecord(
            Long userId,
            Role role,
            CollectionRecordRequestDto dto
    ) {
        // 회사 역할만 등록 허용
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can register records");
        }

        // 1) company 조회
        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        // 2) store 조회 또는 자동 생성 (userId 없이, 이름만)
        Store store = storeRepo.findByName(dto.getStoreName())
                .orElseGet(() -> storeRepo.save(
                        Store.builder()
                                .name(dto.getStoreName())   // userId를 빌더에 넣지 않음 → DB에 NULL
                                .build()
                ));

        // 3) 수거기록 빌더로 생성
        CollectionRecord record = CollectionRecord.builder()
                .company(company)
                .store(store)
                .collectedAt(dto.getCollectedAt())
                .collectedBy(dto.getCollectedBy())
                .volumeLiter(dto.getVolumeLiter())
                .pricePerLiter(dto.getPricePerLiter())
                .totalPrice(dto.getTotalPrice())
                .build();

        // 4) 저장
        recordRepo.save(record);
    }

    /**
     * 3) 수거기록 수정
     */
    @Transactional
    public void updateRecord(
            Long userId,
            Role role,
            Long recordId,
            CollectionRecordUpdateDto dto
    ) {
        // 1) 오직 COMPANY_WORKER만 허용
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can update records");
        }

        // 2) 기존 레코드 조회
        CollectionRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No record with id=" + recordId));

        // 3) 해당 업체(userId)가 작성한 레코드인지 확인
        if (!record.getCompany().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Company worker can only update their own records");
        }

        // 4) 도메인 메서드로 필드 업데이트 (store는 건드리지 않음)
        record.update(
                dto.getCollectedAt(),
                dto.getCollectedBy(),
                dto.getVolumeLiter(),
                dto.getPricePerLiter(),
                dto.getTotalPrice()
        );

        // 5) 트랜잭션 커밋 시 JPA가 변경 감지하여 자동 저장
    }

}
