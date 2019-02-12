package com.zaleskix.parking.services;

import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.DayProfit;
import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.mappers.DayProfitMapper;
import com.zaleskix.parking.models.DayProfitDTO;
import com.zaleskix.parking.repositories.DayProfitRepository;
import com.zaleskix.parking.repositories.DriverRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class DayProfitServiceTest {

    private final String TIME_PATTERN = "HH:mm:ss";
    private final String DATE_FORMAT = "yyyy/MM/dd";
    private final String CURRENCY_TYPE_AS_STRING = "PLN";

    private String currencyDay;
    private String currencyMonth;
    private String currencyYear;

    DayProfitService dayProfitService;

    @Mock
    DriverRepository driverRepository;
    @Mock
    DayProfitRepository dayProfitRepository;
    @Mock
    DayProfitMapper dayProfitMapper;

    @Before
    public void setUp() throws Exception {
        setCurrentDatesToVariables();

        MockitoAnnotations.initMocks(this);
        dayProfitService = new DayProfitServiceImpl(driverRepository, dayProfitRepository, dayProfitMapper);

    }


    @Test
    public void checkThatDateIsValidAndReturnDateAsStringShouldReturnDateAsStringWhenGivenDateIsCorrect() {
        String currencyDate = currencyMonth + "/" + currencyDay + "/" + currencyYear;

        assertEquals(currencyDate, dayProfitService.checkThatDateIsValidAndReturnDateAsString(currencyDate));
    }

    @Test
    public void checkThatDateIsValidAndReturnDateAsStringShouldReturnNullWhenGivenDateIsNotCorrect() {
        assertNull(dayProfitService.checkThatDateIsValidAndReturnDateAsString("wrong data"));
    }

    @Test
    public void getDayProfitAsDTOWithSpecifiedCurrencyIncludedShouldReturnDayProfitAsDTOWhenExistInDatabase() {
        String currencyDate = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);

        DayProfit dayProfit = createDayProfitWithDefaultData(currencyDate);
        DayProfitDTO dayProfitDTO = createDayProfitDTOWithDefaultData(currencyDate);

        when(dayProfitRepository.findByDate(anyString())).thenReturn(Optional.ofNullable(dayProfit));
        when(dayProfitMapper.dayProfitToDayProfitDTO(any())).thenReturn(dayProfitDTO);

        DayProfitDTO returnedDayProfitDTO = dayProfitService.getDayProfitAsDTOWithSpecifiedCurrencyIncluded(currencyDate, CURRENCY_TYPE_AS_STRING);

        assertEquals(currencyDate, returnedDayProfitDTO.getDate());
        assertEquals(BigDecimal.valueOf(1.20), returnedDayProfitDTO.getProfit());
        assertEquals(CurrencyType.PLN, returnedDayProfitDTO.getCurrencyType());
    }

    @Test
    public void getDayProfitAsDTOWithSpecifiedCurrencyIncludedShouldReturnNullWhenDayProfitWithGivenDateDoesNotExist() {
        String currencyDate = currencyMonth + "/" + currencyDay + "/" + currencyYear;

        assertNull(dayProfitService.getDayProfitAsDTOWithSpecifiedCurrencyIncluded(currencyDate, CURRENCY_TYPE_AS_STRING));
    }

    @Test
    public void checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrencyShouldReturnDayProfitAsDTOWhenExistInDatabase() {
        String currencyDate = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);

        DayProfit dayProfit = createDayProfitWithDefaultData(currencyDate);
        DayProfitDTO dayProfitDTO = createDayProfitDTOWithDefaultData(currencyDate);

        when(dayProfitRepository.findByDate(anyString())).thenReturn(Optional.ofNullable(dayProfit));
        when(dayProfitMapper.dayProfitToDayProfitDTO(any())).thenReturn(dayProfitDTO);

        BigDecimal profit = dayProfitService.checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrency(currencyDate, CURRENCY_TYPE_AS_STRING);

        assertEquals(BigDecimal.valueOf(1.20), profit);
    }

    @Test
    public void checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrencyShouldThrowNullPointerExceptionWhenDayProfitWithGivenDateDoesNotExist() {

        String currencyDate = currencyMonth + "/" + currencyDay + "/" + currencyYear;

        boolean thrown = false;
        BigDecimal amountToPay = BigDecimal.valueOf(0);

        try {
            amountToPay = dayProfitService.checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrency(currencyDate, CURRENCY_TYPE_AS_STRING);
        } catch (NullPointerException e) {
            thrown = true;
        }

        assertTrue(thrown);
        assertEquals(BigDecimal.valueOf(0), amountToPay);
    }

    @Test
    public void saveOrUpdateDayProfitWithGivenDateShouldCreateNewDayProfitInDatabaseAndReturnAsDTO() {
        String currencyDate = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);

        DayProfit dayProfit = createDayProfitWithDefaultData(currencyDate);
        DayProfitDTO dayProfitDTO = createDayProfitDTOWithDefaultData(currencyDate);

        when(dayProfitRepository.save(any())).thenReturn(dayProfit);
        when(dayProfitMapper.dayProfitToDayProfitDTO(any())).thenReturn(dayProfitDTO);
        when(dayProfitMapper.dayProfitDTOToDayProfit(any())).thenReturn(dayProfit);


        DayProfitDTO returnedDayProfitDTO = dayProfitService.saveOrUpdateDayProfitWithGivenDate(currencyDate, CurrencyType.PLN);

        assertEquals(currencyDate, returnedDayProfitDTO.getDate());
        assertEquals(BigDecimal.valueOf(1.20), returnedDayProfitDTO.getProfit());
        assertEquals(CurrencyType.PLN, returnedDayProfitDTO.getCurrencyType());
    }

    @Test
    public void saveOrUpdateDayProfitWithGivenDateShouldUpdateDayProfitInDatabaseAndReturnAsDTO() {
        String currencyDate = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);

        Driver driver = new Driver();
        driver.setTransactionDay(currencyDate);
        driver.setAmountToPay(BigDecimal.valueOf(1.21));

        DayProfit existingDayProfit = new DayProfit();
        existingDayProfit.setDate(currencyDate);
        existingDayProfit.setCurrencyType(CurrencyType.PLN);

        DayProfitDTO existingDayProfitDTO = new DayProfitDTO();
        existingDayProfitDTO.setCurrencyType(existingDayProfit.getCurrencyType());
        existingDayProfitDTO.setProfit(driver.getAmountToPay());
        existingDayProfitDTO.setDate(currencyDate);


        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);

        when(driverRepository.findAll()).thenReturn(drivers);
        when(dayProfitRepository.findByDate(anyString())).thenReturn(Optional.ofNullable(existingDayProfit));
        when(dayProfitRepository.save(any())).thenReturn(existingDayProfit);
        when(dayProfitMapper.dayProfitToDayProfitDTO(any())).thenReturn(existingDayProfitDTO);

        DayProfitDTO returnedDayProfitDTO = dayProfitService.saveOrUpdateDayProfitWithGivenDate(currencyDate, CurrencyType.PLN);

        assertEquals(BigDecimal.valueOf(1.21), returnedDayProfitDTO.getProfit());
        assertEquals(CurrencyType.PLN, returnedDayProfitDTO.getCurrencyType());
        assertEquals(currencyDate, returnedDayProfitDTO.getDate());
    }



    //=====================
    // HELPERS METHODS
    //=====================

    private void setCurrentDatesToVariables() {
        currencyDay = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT).substring(8);
        currencyMonth = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT).substring(5,7);
        currencyYear = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT).substring(0,4);
    }

    private String getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(String pattern) {
        Date currDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat(pattern);
        return timeFormat.format(currDate);
    }

    private DayProfitDTO createDayProfitDTOWithDefaultData(String currencyDate) {

        DayProfitDTO dayProfitDTO = new DayProfitDTO();
        dayProfitDTO.setCurrencyType(CurrencyType.PLN);
        dayProfitDTO.setDate(currencyDate);
        dayProfitDTO.setProfit(BigDecimal.valueOf(1.20));
        return dayProfitDTO;
    }

    private DayProfit createDayProfitWithDefaultData (String currencyDate){
        DayProfit dayProfit = new DayProfit();
        dayProfit.setCurrencyType(CurrencyType.PLN);
        dayProfit.setDate(currencyDate);
        dayProfit.setProfit(BigDecimal.valueOf(1.20));

        return dayProfit;
    }

}
