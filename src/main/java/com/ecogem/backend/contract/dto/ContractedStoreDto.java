package com.ecogem.backend.contract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractedStoreDto {

    @JsonProperty("store_id")
    private final Long storeId;

    @JsonProperty("store_name")
    private final String storeName;

    @JsonProperty("address")
    private final String address;

    @JsonProperty("store_phone")
    private final String storePhone;

    @JsonProperty("owner_phone")
    private final String ownerPhone;
}
