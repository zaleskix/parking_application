package com.zaleskix.parking.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.DriverType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {

    private boolean ticketActive;
    private String startTime;
    private String stopTime;
    private BigDecimal amountToPay;
    private DriverType driverType;

    @NotNull
    private String licensePlate;
    private CurrencyType currencyType;

    private String transactionDay;

    @JsonProperty("driver_url")
    private String driverUrl;
}
