package com.ecogem.backend.contract.service;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.companies.domain.Company;
import com.ecogem.backend.companies.repository.CompanyRepository;
import com.ecogem.backend.contract.dto.AddContractedStoreRequestDto;
import com.ecogem.backend.contract.dto.ContractedStoreResponseDto;
import com.ecogem.backend.contract.entity.Contract;
import com.ecogem.backend.contract.repository.ContractRepository;
import com.ecogem.backend.store.domain.Store;
import com.ecogem.backend.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepo;
    private final StoreRepository storeRepo;
    private final CompanyRepository companyRepo;

    public List<ContractedStoreResponseDto> getContractedStore(
            Long userId, Role role, String search
    ) {
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can view contracted stores");
        }

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));
        Long companyId = company.getId();

        List<Contract> contracts = (search != null && !search.isBlank())
                ? contractRepo.findByCompany_IdAndStore_NameContainingIgnoreCase(companyId, search)
                : contractRepo.findByCompany_Id(companyId);

        return contracts.stream()
                .map(c -> ContractedStoreResponseDto.builder()
                        .storeId(c.getStore().getId())
                        .storeName(c.getStore().getName())
                        .address(c.getStore().getAddress())
                        .storePhone(c.getStore().getStorePhone())
                        .ownerPhone(c.getStore().getOwnerPhone())
                        .build()
                ).toList();
    }

    @Transactional
    public void addContractedStore(
            Long userId, Role role, AddContractedStoreRequestDto dto
    ) {
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can add contracted stores");
        }

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        Store store = storeRepo.findByName(dto.getStoreName())
                .orElseGet(() -> storeRepo.save(
                        Store.builder()
                                .name(dto.getStoreName())
                                .address(dto.getAddress())
                                .storePhone(dto.getStorePhone())
                                .ownerPhone(dto.getOwnerPhone())
                                .build()
                ));

        if (!contractRepo.existsByCompanyIdAndStoreId(company.getId(), store.getId())) {
            contractRepo.save(Contract.builder()
                    .company(company)
                    .store(store)
                    .build());
        }
    }

    @Transactional
    public void deleteContractedStore(
            Long userId, Role role, Long storeId
    ) {
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can delete contracts");
        }

        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));
        Long companyId = company.getId();

        boolean exists = contractRepo.existsByCompanyIdAndStoreId(companyId, storeId);
        if (!exists) {
            throw new IllegalArgumentException(
                    "No contract found for companyId=" + companyId + ", storeId=" + storeId
            );
        }

        contractRepo.deleteByCompanyIdAndStoreId(companyId, storeId);
    }
}
