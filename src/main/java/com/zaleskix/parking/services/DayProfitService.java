package com.zaleskix.parking.services;


import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.models.DayProfitDTO;
import java.math.BigDecimal;

public interface DayProfitService {


    DayProfitDTO getDayProfitAsDTOWithSpecifiedCurrencyIncluded(String date, String currencyType);
    BigDecimal checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrency(String date, String currencyType);
    void saveOrUpdateDayProfitWithGivenDate(String date, CurrencyType currencyType);
    String checkThatDateIsValidAndReturnDateAsString(String date);
}
