package com.ecogem.backend.collectionrecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor  // Jackson이 바인딩할 기본 생성자
public class CollectionRecordRequestDto {

    @JsonProperty("collected_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate collectedAt;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("volume_liter")
    private BigDecimal volumeLiter;

    @JsonProperty("price_per_liter")
    private Integer pricePerLiter;

    @JsonProperty("total_price")
    private Integer totalPrice;

    @JsonProperty("collected_by")
    private String collectedBy;

}