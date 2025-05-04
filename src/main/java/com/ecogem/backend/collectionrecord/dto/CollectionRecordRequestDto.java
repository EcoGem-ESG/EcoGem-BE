package com.ecogem.backend.collectionrecord.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public CollectionRecordRequestDto() {}

    // Getters
    public LocalDate getCollectedAt() { return collectedAt; }
    public String    getStoreName()   { return storeName; }
    public BigDecimal getVolumeLiter(){ return volumeLiter; }
    public Integer    getPricePerLiter(){ return pricePerLiter; }
    public Integer    getTotalPrice() { return totalPrice; }
    public String     getCollectedBy(){ return collectedBy; }
}
