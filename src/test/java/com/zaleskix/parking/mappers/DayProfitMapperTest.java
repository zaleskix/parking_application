package com.zaleskix.parking.mappers;

import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.DayProfit;
import com.zaleskix.parking.models.DayProfitDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DayProfitMapperTest {


    public static final CurrencyType CURRENCY = CurrencyType.PLN;
    public static final String DATE = "2018/06/24";
    DayProfitMapper dayProfitMapper = DayProfitMapper.INSTANCE;

    @Test
    public void testDayProfitToDayProfitDTO() throws Exception {

        DayProfit dayProfit = new DayProfit();
        dayProfit.setCurrencyType(CURRENCY);
        dayProfit.setDate(DATE);

        DayProfitDTO dayProfitDTO = dayProfitMapper.dayProfitToDayProfitDTO(dayProfit);

        assertEquals(CURRENCY, dayProfitDTO.getCurrencyType());
        assertEquals(DATE, dayProfitDTO.getDate());

    }

    @Test
    public void testDriverDTOToDriver() throws Exception {

        DayProfitDTO dayProfitDTO = new DayProfitDTO();
        dayProfitDTO.setCurrencyType(CURRENCY);
        dayProfitDTO.setDate(DATE);

        DayProfit dayProfit = dayProfitMapper.dayProfitDTOToDayProfit(dayProfitDTO);

        assertEquals(CURRENCY, dayProfit.getCurrencyType());
        assertEquals(DATE, dayProfit.getDate());

    }
}
