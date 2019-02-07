package com.zaleskix.parking.controllers;


import com.zaleskix.parking.ParkingApplication;
import com.zaleskix.parking.domain.CurrencyType;

import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.domain.DriverType;
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParkingApplication.class)
@WebAppConfiguration
public class DriverControllerTest extends AbstractRestControllerTest {

    private final String TIME_PATTERN = "HH:mm:ss";
    private final String DATE_FORMAT = "yyyy/MM/dd";
    private final String DRIVER_TYPE = "REGULAR";
    private final String CURRENCY_TYPE = "PLN";

    private final BigDecimal STARTED_AMOUNT_TO_PAY = new BigDecimal(0.00);
    private final boolean TICKET_IS_ACTIVE = true;
    private final boolean TICKET_IS_NOT_ACTIVE = false;
    private final CurrencyType PLN_CURRENCY_TYPE = CurrencyType.PLN;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


    @Test
    public void shouldReturnDriverDTOWhenStartParkingMeterWithCorrectLicencePlate() throws Exception {

        String licencePlate = "TEST123";

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", equalTo(getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN))))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(STARTED_AMOUNT_TO_PAY.intValue())))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_ACTIVE)));
    }

    @Test
    public void shouldReturnNullAsResponseBodyWhenStartParkingMeterWithToShortLicencePlate() throws Exception {

        String licencePlate = "x";

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturnNullAsResponseBodyWhenStartParkingMeterWithToLongLicencePlate() throws Exception {

        String licencePlate = "TEST123412356";

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturnTrueWhenDriverStartParkingMeterFirst() throws Exception {

        String licencePlate = "TEST223";

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", equalTo(getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN))))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(STARTED_AMOUNT_TO_PAY.intValue())))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_ACTIVE)));

        mockMvc.perform(get(DriverController.BASE_URL + "/check/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(TICKET_IS_ACTIVE)));
    }

    @Test
    public void shouldReturnFalseWhenDriverNotStartParkingMeterFirst() throws Exception {

        String licencePlate = "TES2T23";

        mockMvc.perform(get(DriverController.BASE_URL + "/check/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void shouldStopParkingMeterCorrectlyWhenDriverStartParkingMeterFirst() throws Exception {

        String licencePlate = "TEST323";
        String startTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN);

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", equalTo(startTime)))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(STARTED_AMOUNT_TO_PAY.intValue())))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_ACTIVE)));

        String stopTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN);
        String transactionDay = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);


        mockMvc.perform(put(DriverController.BASE_URL + "/stop/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", equalTo(startTime)))
                .andExpect(jsonPath("$.stopTime", equalTo(stopTime)))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(1.00)))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_NOT_ACTIVE)))
                .andExpect(jsonPath("$.transactionDay", equalTo(transactionDay)));
    }

    @Test
    public void shouldStopParkingMeterThrowResourceNotFoundExceptionWhenDriverDontStartParkingMeterFirst() throws Exception {

        String licencePlate = "TEST423";

        mockMvc.perform(put(DriverController.BASE_URL + "/stop/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldCheckAmountToPayCorrectlyWhenDriverStartParkingMeterFirst() throws Exception {

        String licencePlate = "TEST523";
        String startTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN);

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startTime", equalTo(startTime)))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(STARTED_AMOUNT_TO_PAY.intValue())))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_ACTIVE)));

        String stopTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN);
        String transactionDay = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);


        mockMvc.perform(put(DriverController.BASE_URL + "/stop/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", equalTo(startTime)))
                .andExpect(jsonPath("$.stopTime", equalTo(stopTime)))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(1.00)))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_NOT_ACTIVE)))
                .andExpect(jsonPath("$.transactionDay", equalTo(transactionDay)));

        mockMvc.perform(get(DriverController.BASE_URL + "/cost/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().string("1.00"));

    }

    @Test
    public void shouldReturnDriverDTOWhenGetDriverInfoIsCalled() throws Exception {

        String licencePlate = "TEST623";
        String startTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_PATTERN);

        mockMvc.perform(post(DriverController.BASE_URL + "/start/" + DRIVER_TYPE + "/" + CURRENCY_TYPE + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL)
                .content(licencePlate))
                .andExpect(status().isCreated());

        mockMvc.perform(get(DriverController.BASE_URL + "/show/" + licencePlate + "/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime", equalTo(startTime)))
                .andExpect(jsonPath("$.licensePlate", equalTo(licencePlate)))
                .andExpect(jsonPath("$.amountToPay", equalTo(0.0)))
                .andExpect(jsonPath("$.currencyType", equalTo(PLN_CURRENCY_TYPE.toString())))
                .andExpect(jsonPath("$.ticketActive", equalTo(TICKET_IS_ACTIVE)));


    }


    //=====================
    // HELPERS METHODS
    //=====================

    private String getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(String pattern) {
        Date currDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat(pattern);
        return timeFormat.format(currDate);
    }

}
