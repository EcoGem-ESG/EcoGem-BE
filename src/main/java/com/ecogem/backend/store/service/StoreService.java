package com.ecogem.backend.store.service;

import com.ecogem.backend.auth.domain.Status;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.auth.repositorty.UserRepository;
import com.ecogem.backend.store.domain.Store;
import com.ecogem.backend.store.dto.StoreRequestDto;
import com.ecogem.backend.store.dto.StoreResponseDto;
import com.ecogem.backend.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreResponseDto registerStore(Long userId, StoreRequestDto dto) {
        // 1. Store 등록
        Store store = Store.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .storePhone(dto.getStorePhone())
                .ownerPhone(dto.getOwnerPhone())
                .deliveryType(dto.getDeliveryType())
                .build();
        storeRepository.save(store);

        // 2. 유저 상태 변경 및 연동
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        user.setStatus(Status.COMPLETE);
        user.setStore(store);
        userRepository.save(user);

        return new StoreResponseDto(true, 200, "STORE_REGISTER_SUCCESS", store.getId());
    }
}
