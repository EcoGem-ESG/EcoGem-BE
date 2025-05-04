package com.ecogem.backend.contract.service;

import com.ecogem.backend.contract.dto.AddContractedStoreRequestDto;
import com.ecogem.backend.contract.dto.ContractedStoreResponseDto;
import com.ecogem.backend.domain.entity.Company;
import com.ecogem.backend.domain.entity.Contract;
import com.ecogem.backend.domain.entity.Role;
import com.ecogem.backend.domain.entity.Store;
import com.ecogem.backend.domain.repository.CompanyRepository;
import com.ecogem.backend.domain.repository.ContractRepository;
import com.ecogem.backend.domain.repository.StoreRepository;
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

    public List<ContractedStoreResponseDto> getContractedStore(Long userId, Role role, String search) {
        if(role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can view contracted stores");
        }

        List<Contract> contracts;
        if (search != null && !search.isBlank()) {
            contracts = contractRepo
                    .findByCompany_UserIdAndStore_NameContainingIgnoreCase(userId, search);
        } else {
            contracts = contractRepo.findByCompany_UserId(userId);
        }

        return contracts.stream()
                .map(c -> ContractedStoreResponseDto.builder()
                        .storeId(c.getStore().getId())
                        .storeName(c.getStore().getName())
                        .address(c.getStore().getAddress())
                        .storePhone(c.getStore().getStorePhone())
                        .ownerPhone(c.getStore().getOwnerPhone())
                        .build()
                )
                .toList();
    }


    @Transactional
    public void addContractedStore(
            Long userId,
            Role role,
            AddContractedStoreRequestDto dto
    ) {
        // 1) 권한 체크: 오직 COMPANY_WORKER
        if (role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can add contracted stores");
        }

        // 2) 회사 조회
        Company company = companyRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No company for user_id=" + userId));

        // 3) 가게 조회 or 생성
        Store store = storeRepo.findByName(dto.getStoreName())
                .orElseGet(() -> {
                    // 신규 가게: userId 없이 입력된 정보로만 생성
                    return storeRepo.save(Store.builder()
                            .name(dto.getStoreName())
                            .address(dto.getAddress())
                            .storePhone(dto.getStorePhone())
                            .ownerPhone(dto.getOwnerPhone())
                            .build());
                });

        // 4) 계약 테이블에 없으면 추가
        boolean exists = contractRepo.existsByCompany_IdAndStore_Id(
                company.getId(), store.getId());
        if (!exists) {
            contractRepo.save(Contract.builder()
                    .company(company)
                    .store(store)
                    .build());
        }
    }


}
