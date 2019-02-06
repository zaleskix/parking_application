package com.zaleskix.parking.services;


import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.DayProfit;
import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.mappers.DayProfitMapper;
import com.zaleskix.parking.models.DayProfitDTO;
import com.zaleskix.parking.repositories.DayProfitRepository;
import com.zaleskix.parking.repositories.DriverRepository;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;


@Service
public class DayProfitServiceImpl implements DayProfitService {

    private static final String BASE_URL = "/day";
    private Logger logger = LoggerFactory.getLogger(DayProfitServiceImpl.class);
    
    private final DriverRepository driverRepository;
    private final DayProfitRepository dayProfitRepository;
    private final DayProfitMapper dayProfitMapper;

    public DayProfitServiceImpl(DriverRepository driverRepository, DayProfitRepository dayProfitRepository, DayProfitMapper dayProfitMapper) {
        this.driverRepository = driverRepository;
        this.dayProfitRepository = dayProfitRepository;
        this.dayProfitMapper = dayProfitMapper;
    }


    @Override
    public DayProfitDTO getDayProfitAsDTOWithSpecifiedCurrencyIncluded(String date, String currencyType) {

        return dayProfitRepository.findByDate(date)
                .map(dayProfitMapper::dayProfitToDayProfitDTO)
                .map(dayProfitDTOreturned -> {
                    dayProfitDTOreturned
                            .setDayUrl(getDayProfitBaseRestAPIUrl(date));
                    dayProfitDTOreturned.setCurrencyType(CurrencyType.valueOf(currencyType));
                    return dayProfitDTOreturned;
                })
                .orElseThrow(ResourceNotFoundException::new);

    }

    @Override
    public BigDecimal checkAmountOfProfitOnTheGivenDayWithSpecifiedCurrency(String date, String currencyType){
        return getDayProfitAsDTOWithSpecifiedCurrencyIncluded(date, currencyType).getProfit();
    }

    @Override
    public void saveOrUpdateDayProfitWithGivenDate(String date, CurrencyType currencyType) {
        Optional<DayProfit> optionalDayProfit = dayProfitRepository.findByDate(date);

        if(optionalDayProfit.isPresent()) {
            updateDayProfitWithSpecifiedCurrency(date, currencyType);
        } else {
            createNewDayWithInitialValuesAndSaveInDatabase(date);
        }

    }

    @Override
    public String checkThatDateIsValidAndReturnDateAsString(String date) {

        if (isCorrectDate(date)){
            return date;
        }

        return null;
    }

    private void updateDayProfitWithSpecifiedCurrency(String date, CurrencyType currencyType) {
        
        if (dayProfitRepository.findByDate(date).isPresent()) {
            DayProfit dayProfit = dayProfitRepository.findByDate(date).get();
            dayProfit.setProfit(calculateAndReturnDayProfitForGivenDayAndWithGivenCurrencyType(date, currencyType).getAmount());
            saveDayWithGivenDataInDatabaseAndReturnDTO(dayProfit);
        } else {
            logger.error("Given data is not valid. Day profit with given data does not exist.");
        }

    }

    private DayProfitDTO createNewDayWithInitialValuesAndSaveInDatabase(String date) {

        DayProfitDTO dayProfitDTO = new DayProfitDTO();
        dayProfitDTO.setDate(date);
        dayProfitDTO.setProfit(BigDecimal.valueOf(0));

        return saveDayWithGivenDataInDatabaseAndReturnDTO(dayProfitMapper.dayProfitDTOToDayProfit(dayProfitDTO));
    }

    private DayProfitDTO saveDayWithGivenDataInDatabaseAndReturnDTO(DayProfit dayProfit) {

        dayProfit.setDate(dayProfit.getDate());
        DayProfit savedDay = dayProfitRepository.save(dayProfit);
        DayProfitDTO returnedDayDTO = dayProfitMapper.dayProfitToDayProfitDTO(savedDay);
        returnedDayDTO.setDayUrl(getDayProfitBaseRestAPIUrl(savedDay.getDate()));

        return returnedDayDTO;
    }

    private Money calculateAndReturnDayProfitForGivenDayAndWithGivenCurrencyType(String date, CurrencyType currencyType) {

        Iterable<Driver> drivers = driverRepository.findAll();

        Money profit = Money.parse(currencyType.toString() + " 0.0");

        for (Driver driver : drivers) {
            if (Objects.equals(driver.getTransactionDay(), date)) {
                profit.plus(driver.getAmountToPay());
            }
        }

        return profit;
    }

    private String getDayProfitBaseRestAPIUrl(String date) {
        return BASE_URL + date;
    }

    private boolean isCorrectDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date formattedDate =  new Date();

        try {
            formattedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            logger.error("Given date is not valid. Error while parse to Date object");
            return false;
        }

        return true;
    }

}