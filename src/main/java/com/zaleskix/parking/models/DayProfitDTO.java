package com.zaleskix.parking.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zaleskix.parking.domain.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayProfitDTO {

    private BigDecimal profit;
    private String date;
    private CurrencyType currencyType;

    @JsonProperty("day_url")
    private String dayUrl;
}
