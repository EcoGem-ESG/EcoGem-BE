package com.ecogem.backend.contract.service;

import com.ecogem.backend.contract.dto.ContractedStoreDto;
import com.ecogem.backend.domain.entity.Contract;
import com.ecogem.backend.domain.entity.Role;
import com.ecogem.backend.domain.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepo;

    public List<ContractedStoreDto> getContractedStore(Long userId, Role role) {
        if(role != Role.COMPANY_WORKER) {
            throw new IllegalArgumentException("Only COMPANY_WORKER can view contracted stores");
        }

        List<Contract> contracts = contractRepo.findByCompany_UserId(userId);

        return contracts.stream()
                .map(c -> ContractedStoreDto.builder()
                        .storeId(c.getStore().getId())
                        .storeName(c.getStore().getName())
                        .address(c.getStore().getAddress())
                        .storePhone(c.getStore().getStorePhone())
                        .ownerPhone(c.getStore().getOwnerPhone())
                        .build()
                )
                .toList();
    }



}
