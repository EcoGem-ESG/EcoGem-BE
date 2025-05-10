package com.ecogem.backend.mypage.service;

import com.ecogem.backend.auth.domain.Role;
import com.ecogem.backend.auth.domain.User;
import com.ecogem.backend.companies.domain.Company;
import com.ecogem.backend.companies.repository.CompanyRepository;
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
        if (user.getRole() == Role.COMPANY_OWNER) {
            Company company = companyRepository.findByUser(user);
            return new CompanyMypageResponse(company);
        } else {
            Store store = storeRepository.findByUser(user);
            return new StoreMypageResponse(store);
        }
    }

    public void updateMypage(User user, MypageUpdateRequest request) {
        if (user.getRole() == Role.COMPANY_OWNER) {
            Company company = companyRepository.findByUser(user);
            company.setAddress(request.getAddress());
            company.setManagerName(request.getManagerName());
            company.setCompanyPhone(request.getCompanyPhone());
            company.setWasteTypes(request.getWasteTypes());
        } else {
            Store store = storeRepository.findByUser(user);
            store.setAddress(request.getAddress());
            store.setStorePhone(request.getStorePhone());
            store.setOwnerPhone(request.getOwnerPhone());
            store.setDeliveryType(Store.DeliveryType.valueOf(request.getDeliveryType().toUpperCase()));

        }
    }
}
