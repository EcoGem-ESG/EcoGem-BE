package com.ecogem.backend.collectionrecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionRecordResponseDto {


    @JsonProperty("collected_at")
    private final LocalDate collectedAt;

    @JsonProperty("collected_by")
    private final String collectedBy;

    @JsonProperty("store_name")
    private final String storeName;

    @JsonProperty("volume_liter")
    private final BigDecimal volumeLiter;

    @JsonProperty("price_per_liter")
    private final Integer pricePerLiter;

    @JsonProperty("total_price")
    private final Integer totalPrice;

}
