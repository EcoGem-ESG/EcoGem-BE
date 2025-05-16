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
     * 1) Retrieve collection records
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
                // Fetch Store by userId
                Store store = storeRepo.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No store found for user_id=" + userId));
                Long storeId = store.getId();
                yield hasRange
                        ? recordRepo.findByStore_IdAndCollectedAtBetweenOrderByCollectedAtDesc(storeId, start, end)
                        : recordRepo.findByStore_IdOrderByCollectedAtDesc(storeId);
            }

            case COMPANY_WORKER -> {
                // Fetch Company by userId
                Company company = companyRepo.findByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("No company found for user_id=" + userId));
                Long companyId = company.getId();
                yield hasRange
                        ? recordRepo.findByCompany_IdAndCollectedAtBetweenOrderByCollectedAtDesc(companyId, start, end)
                        : recordRepo.findByCompany_IdOrderByCollectedAtDesc(companyId);
            }

            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };

        return records.stream()
                .map(r -> {
                    String displayName = (role == Role.COMPANY_WORKER)
                            ? r.getStore().getName()
                            : r.getCompany().getName();
                    return CollectionRecordResponseDto.builder()
                            .recordId(r.getId())
                            .collectedAt(r.getCollectedAt())
                            .collectedBy(r.getCollectedBy())
                            .storeName(displayName)
                            .volumeLiter(r.getVolumeLiter())
                            .pricePerLiter(r.getPricePerLiter())
                            .totalPrice(r.getTotalPrice())
                            .build();
                })
                .toList();
    }

    /**
     * 2) Register a new collection record (COMPANY_WORKER only)
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
                .orElseThrow(() -> new IllegalArgumentException("No company found for user_id=" + userId));

        Store store = storeRepo.findByName(dto.getStoreName())
                .orElseGet(() -> storeRepo.save(
                        Store.builder()
                                .name(dto.getStoreName())
                                .build()
                ));

        boolean contractExists = contractRepo
                .existsByCompanyIdAndStoreId(company.getId(), store.getId());

        if (!contractExists) {
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
     * 3) Update an existing collection record (COMPANY_WORKER only)
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
                .orElseThrow(() -> new IllegalArgumentException("No record found with id=" + recordId));

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company found for user_id=" + userId));

        if (!record.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Cannot update records from another company");
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
     * 4) Delete a collection record and remove contract if no more records exist
     */
    @Transactional
    public void deleteRecord(Long userId, Role role, Long recordId) {
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can delete records");
        }

        CollectionRecord record = recordRepo.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No record found with id=" + recordId));

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company found for user_id=" + userId));

        if (!record.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("Cannot delete records from another company");
        }

        Long companyId = company.getId();
        Long storeId   = record.getStore().getId();

        recordRepo.delete(record);

        boolean stillExists = recordRepo.existsByCompany_IdAndStore_Id(companyId, storeId);
        if (!stillExists) {
            contractRepo.deleteByCompanyIdAndStoreId(companyId, storeId);
        }
    }
}
