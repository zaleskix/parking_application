package com.zaleskix.parking.controllers;

import com.zaleskix.parking.ParkingApplication;
import com.zaleskix.parking.domain.CurrencyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParkingApplication.class)
@WebAppConfiguration
public class DayProfitControllerTest extends AbstractRestControllerTest {

    private final String DATE_FORMAT = "yyyy/MM/dd";
    private final CurrencyType PLN_CURRENCY_TYPE = CurrencyType.PLN;

    private String currencyDay;
    private String currencyMonth;
    private String currencyYear;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        setCurrentDatesToVariables();
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldReturnDayProfitAsBigDecimalCorrectly() throws Exception {

        String licencePlate = "TEST123";
        runStartAndStopRequestForDriverWithGivenLicencePlate(licencePlate);


        String request = DayProfitController.BASE_URL + "/" + currencyYear + "/" + currencyMonth + "/" +
                currencyDay + "/profit/" + PLN_CURRENCY_TYPE + "/";

        mockMvc.perform(get(request)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("1.00"));
    }


    @Test
    public void shouldReturnDayProfitAsBigDecimalCorrectlyWhenManyDriversPayForParking() throws Exception {

        String licencePlate1 = "TEST213";
        runStartAndStopRequestForDriverWithGivenLicencePlate(licencePlate1);

        String licencePlate2 = "TEST223";
        runStartAndStopRequestForDriverWithGivenLicencePlate(licencePlate2);

        String request = DayProfitController.BASE_URL + "/" + currencyYear + "/" + currencyMonth + "/" +
                currencyDay + "/profit/" + PLN_CURRENCY_TYPE + "/";

        mockMvc.perform(get(request)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("3.00"));
    }


    //("/{year}/{month}/{day}/show/currencyType")
    @Test
    public void shouldReturnDayProfitDTOCorrectly() throws Exception {

        String licencePlate = "TEST323";
        runStartAndStopRequestForDriverWithGivenLicencePlate(licencePlate);

        String request = DayProfitController.BASE_URL + "/" + currencyYear + "/" + currencyMonth + "/" +
                currencyDay + "/show/" + PLN_CURRENCY_TYPE + "/";

        mockMvc.perform(get(request)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profit", equalTo(4.00)))
                .andExpect(jsonPath("$.date", equalTo(getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT))))
                .andExpect(jsonPath("$.currencyType", equalTo("PLN")));

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

    private void runStartAndStopRequestForDriverWithGivenLicencePlate (String licencePlate) throws Exception {
        startParkingMeterForRegularDriverWithLicencePlate(licencePlate);
        stopParkingMeterWithLicencePlate(licencePlate);
    }

    private void startParkingMeterForRegularDriverWithLicencePlate(String licencePlate) throws Exception {

        mockMvc.perform(post(DriverController.BASE_URL + "/start/REGULAR/PLN/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated());
    }

    private void stopParkingMeterWithLicencePlate(String licencePlate) throws Exception {

        mockMvc.perform(put(DriverController.BASE_URL + "/stop/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk());
    }
}
