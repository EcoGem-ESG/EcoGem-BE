package com.ecogem.backend.collectionrecord.service;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordRequestDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordResponseDto;
import com.ecogem.backend.collectionrecord.dto.CollectionRecordUpdateDto;
import com.ecogem.backend.collectionrecord.entity.CollectionRecord;
import com.ecogem.backend.collectionrecord.repository.CollectionRecordRepository;
import com.ecogem.backend.company.domain.Company;
import com.ecogem.backend.company.repository.CompanyRepository;
import com.ecogem.backend.contract.entity.Contract;
import com.ecogem.backend.contract.repository.ContractRepository;
import com.ecogem.backend.store.domain.Store;
import com.ecogem.backend.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionRecordService {

    private final CollectionRecordRepository recordRepo;
    private final StoreRepository storeRepo;
    private final CompanyRepository companyRepo;
    private final ContractRepository contractRepo;

    /**
     * 1) 수거기록 조회
     */
    @Transactional(readOnly = true)
    public List<CollectionRecordResponseDto> getRecordsForUser(
            Long userId,
            Role role,
            LocalDate start,
            LocalDate end
    ) {
        boolean hasRange = (start != null && end != null);

        List<CollectionRecord> records = switch (role) {
            case STORE_OWNER -> {
                // ① userId로 Store 조회
                Store store = storeRepo.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No store for user_id=" + userId));
                Long storeId = store.getId();
                yield hasRange
                        ? recordRepo.findByStore_IdAndCollectedAtBetweenOrderByCollectedAtDesc(storeId, start, end)
                        : recordRepo.findByStore_IdOrderByCollectedAtDesc(storeId);
            }

            case COMPANY_WORKER -> {
                // ① userId로 Company 조회
                Company company = companyRepo.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));
                Long companyId = company.getId();
                yield hasRange
                        ? recordRepo.findByCompany_IdAndCollectedAtBetweenOrderByCollectedAtDesc(companyId, start, end)
                        : recordRepo.findByCompany_IdOrderByCollectedAtDesc(companyId);
            }

            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };

        return records.stream()
                .map(r -> {
                    String nameToShow = (role == Role.COMPANY_WORKER)
                            ? r.getStore().getName()
                            : r.getCompany().getName();
                    return CollectionRecordResponseDto.builder()
                            .recordId(r.getId())
                            .collectedAt(r.getCollectedAt())
                            .collectedBy(r.getCollectedBy())
                            .storeName(nameToShow)
                            .volumeLiter(r.getVolumeLiter())
                            .pricePerLiter(r.getPricePerLiter())
                            .totalPrice(r.getTotalPrice())
                            .build();
                })
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
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can register records");
        }

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        Store store = storeRepo.findByName(dto.getStoreName())
                .orElseGet(() -> storeRepo.save(
                        Store.builder()
                                .name(dto.getStoreName())
                                .build()
                ));

        boolean existsContract = contractRepo
                .existsByCompanyIdAndStoreId(company.getId(), store.getId());

        if (!existsContract) {
            Contract contract = Contract.builder()
                    .company(company)
                    .store(store)
                    .build();
            contractRepo.save(contract);
        }

        CollectionRecord record = CollectionRecord.builder()
                .company(company)
                .store(store)
                .collectedAt(dto.getCollectedAt())
                .collectedBy(dto.getCollectedBy())
                .volumeLiter(dto.getVolumeLiter())
                .pricePerLiter(dto.getPricePerLiter())
                .totalPrice(dto.getTotalPrice())
                .build();

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
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can update records");
        }

        CollectionRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No record with id=" + recordId));

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        if (!record.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Company worker can only update their own records");
        }

        record.update(
                dto.getCollectedAt(),
                dto.getCollectedBy(),
                dto.getVolumeLiter(),
                dto.getPricePerLiter(),
                dto.getTotalPrice()
        );
    }

    /**
     * 4) 수거기록 삭제
     */
    @Transactional
    public void deleteRecord(Long userId, Role role, Long recordId) {
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can delete records");
        }

        CollectionRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No record with id=" + recordId));

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        if (!record.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Cannot delete others' records");
        }

        Long companyId = company.getId();
        Long storeId   = record.getStore().getId();

        recordRepo.delete(record);

        boolean exists = recordRepo.existsByCompany_IdAndStore_Id(companyId, storeId);
        if (!exists) {
            contractRepo.deleteByCompanyIdAndStoreId(companyId, storeId);
        }
    }
}
