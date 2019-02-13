package com.zaleskix.parking.controllers;

import com.zaleskix.parking.models.DayProfitDTO;
import com.zaleskix.parking.services.DayProfitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(DayProfitController.BASE_URL)
public class DayProfitController {

    public static final String BASE_URL = "/day";

    private final DayProfitService dayProfitService;

    public DayProfitController(DayProfitService dayProfitService) {
        this.dayProfitService = dayProfitService;
    }

    @GetMapping("/{year}/{month}/{day}/profit/{currencyType}/")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal checkDayProfitPLN(@PathVariable String year, @PathVariable String month,
                                       @PathVariable String day, @PathVariable String currencyType) {

        String date = year + "/" + month + "/" + day;
        return dayProfitService.checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrency(date, currencyType);
    }

    @GetMapping("/{year}/{month}/{day}/show/{currencyType}/")
    @ResponseStatus(HttpStatus.OK)
    public DayProfitDTO getDayProfitInfo(@PathVariable String year, @PathVariable String month,
                                         @PathVariable String day, @PathVariable String currencyType) {

        String date = year + "/" + month + "/" + day;
        date = dayProfitService.checkThatDateIsValidAndReturnDateAsString(date);
        return dayProfitService.getDayProfitAsDTOWithSpecifiedCurrencyIncluded(date, currencyType);
    }


}