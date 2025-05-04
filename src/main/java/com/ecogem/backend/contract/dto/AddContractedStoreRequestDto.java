package com.ecogem.backend.contract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddContractedStoreRequestDto {

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("store_phone")
    private String storePhone;

    @JsonProperty("owner_phone")
    private String ownerPhone;

}
