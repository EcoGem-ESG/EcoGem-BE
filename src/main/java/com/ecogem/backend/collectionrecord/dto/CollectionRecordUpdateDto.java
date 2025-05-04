package com.ecogem.backend.collectionrecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CollectionRecordUpdateDto {

    @JsonProperty("collected_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate collectedAt;

    @JsonProperty("volume_liter")
    private BigDecimal volumeLiter;

    @JsonProperty("price_per_liter")
    private Integer pricePerLiter;

    @JsonProperty("total_price")
    private Integer totalPrice;

    @JsonProperty("collected_by")
    private String collectedBy;

}

