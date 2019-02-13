package com.zaleskix.parking.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity

public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private DriverType driverType = DriverType.REGULAR;
    private CurrencyType currencyType = CurrencyType.PLN;
    private boolean ticketActive = false;
    private String startTime;
    private String stopTime;
    @NotNull
    private String licensePlate;
    private BigDecimal amountToPay = new BigDecimal(0.00);

    private String transactionDay;
}
