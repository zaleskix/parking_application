package com.zaleskix.parking.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class DayProfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private BigDecimal profit;
    private CurrencyType currencyType = CurrencyType.PLN;
    private String date;
}
