package com.ecogem.backend.mypage.service;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.company.domain.Company;
import com.ecogem.backend.company.repository.CompanyRepository;
import com.ecogem.backend.mypage.dto.CompanyMypageResponse;
import com.ecogem.backend.mypage.dto.MypageUpdateRequest;
import com.ecogem.backend.mypage.dto.StoreMypageResponse;
import com.ecogem.backend.store.domain.Store;
import com.ecogem.backend.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final CompanyRepository companyRepository;
    private final StoreRepository storeRepository;

    public Object getMypage(User user) {
        if (user.getRole() == Role.COMPANY_WORKER) {
            Company company = companyRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No company for userId=" + user.getId()));
            return new CompanyMypageResponse(company);
        } else {
            Store store = storeRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No store for userId=" + user.getId()));
            return new StoreMypageResponse(store);
        }
    }

    public void updateMypage(User user, MypageUpdateRequest request) {
        if (user.getRole() == Role.COMPANY_WORKER) {
            Company company = companyRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No company for userId=" + user.getId()));
            company.setAddress(request.getAddress());
            company.setManagerName(request.getManagerName());
            company.setCompanyPhone(request.getCompanyPhone());
            company.setWasteTypes(request.getWasteTypes());
        } else {
            Store store = storeRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No store for userId=" + user.getId()));
            store.updateProfile(
                    request.getAddress(),
                    request.getStorePhone(),
                    request.getOwnerPhone(),
                    Store.DeliveryType.valueOf(request.getDeliveryType().toUpperCase())
            );
        }
    }
}