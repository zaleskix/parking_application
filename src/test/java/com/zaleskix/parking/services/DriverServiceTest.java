package com.zaleskix.parking.services;

import com.zaleskix.parking.controllers.DriverController;
import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.domain.DriverType;
import com.zaleskix.parking.mappers.DriverMapper;
import com.zaleskix.parking.models.DriverDTO;
import com.zaleskix.parking.repositories.DriverRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DriverServiceTest {


    private final String TIME_FORMAT = "HH:mm:ss";
    private final String DATE_FORMAT = "yyyy/MM/dd";
    private final String CURRENCY_TYPE_AS_STRING = "PLN";
    private final String CORRECT_LICENCE_PLATE = "TEST1234";
    private final String TO_LONG_LICENCE_PLATE = "TEST123456789";
    private final String TO_SHORT_LICENCE_PLATE = "T";
    private final String INCORRECT_LICENCE_PLATE = "!%!@$$@!@%@";
    private final DriverType REGULAR_DRIVER_TYPE = DriverType.REGULAR;
    private final CurrencyType PLN_CURRENCY_TYPE = CurrencyType.PLN;

    private String currentTime;
    private String currentDate;
    private String currencyYear;

    DriverService driverService;

    @Mock
    DriverRepository driverRepository;
    @Mock
    DriverMapper driverMapper;
    @Mock
    DayProfitService dayProfitService;

    @Before
    public void setUp() throws Exception {
        setCurrentDateAndTimeToVariables();

        MockitoAnnotations.initMocks(this);
        driverService = new DriverServiceImpl(driverMapper, dayProfitService, driverRepository);

    }

    @Test
    public void shouldStartParkingMeterAndReturnDTOWhenLicencePlateIsValidAndDriverNotExistInDatabase() {
        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        Optional<Driver> optionalDriver = Optional.empty();

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(optionalDriver);
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);
        when(driverRepository.save(any())).thenReturn(driver);

        DriverDTO returnedDTO = driverService.startParkingMeterByLicensePlate(CORRECT_LICENCE_PLATE, REGULAR_DRIVER_TYPE.toString(), CURRENCY_TYPE_AS_STRING);

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());
    }

    @Test
    public void shouldStartParkingMeterAndReturnDTOWhenLicencePlateIsValidAndDriverExistInDatabase() {
        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);
        when(driverRepository.save(any())).thenReturn(driver);

        DriverDTO returnedDTO = driverService.startParkingMeterByLicensePlate(CORRECT_LICENCE_PLATE, REGULAR_DRIVER_TYPE.toString(), CURRENCY_TYPE_AS_STRING);

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());
    }

    @Test
    public void shouldStartParkingMeterAndReturnNullWhenLicencePlateNotCorrect() {

        //Licence plate is to short
        DriverDTO returnedDTO = driverService.startParkingMeterByLicensePlate(TO_SHORT_LICENCE_PLATE,
                REGULAR_DRIVER_TYPE.toString(), CURRENCY_TYPE_AS_STRING);

        assertNull(returnedDTO);

        //Licence plate is to long
        returnedDTO = driverService.startParkingMeterByLicensePlate(TO_LONG_LICENCE_PLATE,
                REGULAR_DRIVER_TYPE.toString(), CURRENCY_TYPE_AS_STRING);

        assertNull(returnedDTO);

        //Licence plate with special symbols
        returnedDTO = driverService.startParkingMeterByLicensePlate(INCORRECT_LICENCE_PLATE,
                REGULAR_DRIVER_TYPE.toString(), CURRENCY_TYPE_AS_STRING);

        assertNull(returnedDTO);


    }

    @Test
    public void shouldStopParkingMeterAndThrowNullPointerExceptionWhenLicencePlateIsValidAndDriverNotExistInDatabase() {

        Optional<Driver> optionalDriver = Optional.empty();
        boolean thrown = false;

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(optionalDriver);

        try {
            driverService.stopParkingMeterByLicensePlate(CORRECT_LICENCE_PLATE);
        } catch (ResourceNotFoundException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    @Test
    public void shouldStopParkingMeterAndReturnDTOWhenLicencePlateIsValidAndDriverExistInDatabase() {
        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.findById(anyString())).thenReturn(Optional.ofNullable(driver));
        when(dayProfitService.saveOrUpdateDayProfitWithGivenDate(anyString(), any())).thenReturn(null);
        when(driverRepository.save(any())).thenReturn(driver);
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);

        DriverDTO returnedDTO = driverService.stopParkingMeterByLicensePlate(CORRECT_LICENCE_PLATE);

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());
    }

    @Test
    public void shouldStopParkingMeterAndReturnNullWhenLicencePlateNotCorrect() {

        //Licence plate is to short
        DriverDTO returnedDTO = driverService.stopParkingMeterByLicensePlate(TO_SHORT_LICENCE_PLATE);
        assertNull(returnedDTO);

        //Licence plate is to long
        returnedDTO = driverService.stopParkingMeterByLicensePlate(TO_LONG_LICENCE_PLATE);
        assertNull(returnedDTO);

        //Licence plate with special symbols
        returnedDTO = driverService.stopParkingMeterByLicensePlate(INCORRECT_LICENCE_PLATE);
        assertNull(returnedDTO);


    }

    @Test
    public void shouldStopParkingMeterAndThrowNullPointerExceptionWhenDriverNotExistInDatabaseWithGivenID() {

        Optional<Driver> optionalDriver = Optional.empty();
        boolean thrown = false;

        when(driverRepository.findById(anyString())).thenReturn(optionalDriver);

        try {
            driverService.stopParkingMeterById("123");
        } catch (ResourceNotFoundException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    @Test
    public void shouldStopParkingMeterAndReturnDTOWhenDriverExistInDatabaseWithGivenID() {
        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        when(driverRepository.findById(anyString())).thenReturn(Optional.ofNullable(driver));
        when(dayProfitService.saveOrUpdateDayProfitWithGivenDate(anyString(), any())).thenReturn(null);
        when(driverRepository.save(any())).thenReturn(driver);
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);

        DriverDTO returnedDTO = driverService.stopParkingMeterById("123");

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());
    }

    @Test
    public void shouldCheckTicketIsValidByLicensePlateReturnNullWhenLicencePlateNotCorrect() {

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidByLicensePlate(TO_SHORT_LICENCE_PLATE);
        assertNull(ticketIsValid);

        //Licence plate is to long
        ticketIsValid = driverService.checkTicketIsValidByLicensePlate(TO_LONG_LICENCE_PLATE);
        assertNull(ticketIsValid);

        //Licence plate with special symbols
        ticketIsValid = driverService.checkTicketIsValidByLicensePlate(INCORRECT_LICENCE_PLATE);
        assertNull(ticketIsValid);


    }

    @Test
    public void shouldCheckTicketIsValidByLicensePlateReturnNullWhenDriverWithGivenLicencePlateNotExistInDatabase() {

        Optional<Driver> optionalDriver = Optional.empty();

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(optionalDriver);

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidByLicensePlate(CORRECT_LICENCE_PLATE);
        assertNull(ticketIsValid);
    }

    @Test
    public void shouldCheckTicketIsValidByLicensePlateReturnTrueWhenDriverWithGivenLicencePlateHasTicketActive() {

        Driver driver = new Driver();
        driver.setTicketActive(true);

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(Optional.ofNullable(driver));

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidByLicensePlate(CORRECT_LICENCE_PLATE);
        assertTrue(ticketIsValid);
    }

    @Test
    public void shouldCheckTicketIsValidByLicensePlateReturnFalseWhenDriverWithGivenLicencePlateHasNotTicketActive() {

        Driver driver = new Driver();
        driver.setTicketActive(false);

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(Optional.ofNullable(driver));

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidByLicensePlate(CORRECT_LICENCE_PLATE);
        assertFalse(ticketIsValid);
    }

    @Test
    public void shouldCheckTicketIsValidByIdReturnTrueWhenDriverWithGivenIdHasTicketActive() {

        Driver driver = new Driver();
        driver.setTicketActive(true);

        when(driverRepository.findById(anyString())).thenReturn(Optional.ofNullable(driver));

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidById(CORRECT_LICENCE_PLATE);
        assertTrue(ticketIsValid);
    }

    @Test
    public void shouldCheckTicketIsValidByIdReturnFalseWhenDriverWithGivenIdHasNotTicketActive() {

        Driver driver = new Driver();
        driver.setTicketActive(false);

        when(driverRepository.findById(anyString())).thenReturn(Optional.ofNullable(driver));

        //Licence plate is to short
        Boolean ticketIsValid = driverService.checkTicketIsValidById(CORRECT_LICENCE_PLATE);
        assertFalse(ticketIsValid);
    }

    @Test
    public void shouldFindDriverByLicencePlateReturnNullWhenLicencePlateNotCorrect() {

        //Licence plate is to short
        DriverDTO driverDTO = driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(TO_SHORT_LICENCE_PLATE);
        assertNull(driverDTO);

        //Licence plate is to long
        driverDTO = driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(TO_LONG_LICENCE_PLATE);
        assertNull(driverDTO);

        //Licence plate with special symbols
        driverDTO = driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(INCORRECT_LICENCE_PLATE);
        assertNull(driverDTO);


    }

    @Test
    public void shouldFindDriverByLicencePlateThrowResourceNotFoundExceptionWhenDriverWithGivenLicencePlateNotExistInDatabase() {

        Optional<Driver> optionalDriver = Optional.empty();
        boolean thrown = false;

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(optionalDriver);

        try {
            driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(CORRECT_LICENCE_PLATE);
        } catch (ResourceNotFoundException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    @Test
    public void shouldFindDriverByLicencePlateReturnDTOWhenDriverWithGivenLicencePlateExistInDatabase() {

        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        when(driverRepository.findByLicensePlate(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);
        when(driverMapper.driverDTOToDriver(any())).thenReturn(driver);

        DriverDTO returnedDTO = driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(CORRECT_LICENCE_PLATE);

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());

    }

    @Test
    public void shouldFindDriverByIdThrowResourceNotFoundExceptionWhenDriverWithGivenIdNotExistInDatabase() {

        Optional<Driver> optionalDriver = Optional.empty();
        boolean thrown = false;

        when(driverRepository.findById(anyString())).thenReturn(optionalDriver);

        try {
            driverService.findDriverByIdAndReturnDriverInfoAsDTO(CORRECT_LICENCE_PLATE);
        } catch (ResourceNotFoundException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    @Test
    public void shouldFindDriverByIdReturnDTOWhenDriverWithGivenIdExistInDatabase() {

        Driver driver = createDriverWithDefauldData();
        DriverDTO mappedDriver = createDriverDTOWithDefauldData();

        when(driverRepository.findById(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverMapper.driverToDriverDTO(any())).thenReturn(mappedDriver);
        when(driverMapper.driverDTOToDriver(any())).thenReturn(driver);

        DriverDTO returnedDTO = driverService.findDriverByIdAndReturnDriverInfoAsDTO(CORRECT_LICENCE_PLATE);

        assertEquals(REGULAR_DRIVER_TYPE, returnedDTO.getDriverType());
        assertEquals(PLN_CURRENCY_TYPE, returnedDTO.getCurrencyType());
        assertEquals(CORRECT_LICENCE_PLATE, returnedDTO.getLicensePlate());
        assertEquals(currentTime, returnedDTO.getStartTime());
        assertEquals(currentDate, returnedDTO.getTransactionDay());
        assertNull(returnedDTO.getStopTime());
        assertEquals(BigDecimal.valueOf(0), returnedDTO.getAmountToPay());
        assertEquals(DriverController.BASE_URL + "/1", returnedDTO.getDriverUrl());

    }


    //=====================
    // HELPERS METHODS
    //=====================

    private void setCurrentDateAndTimeToVariables() {
        currentDate = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(DATE_FORMAT);
        currentTime = getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(TIME_FORMAT);
    }

    private String getCurrentDateTimeAndReturnDateTimeAsStringWithPattern(String pattern) {
        Date currDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat(pattern);
        return timeFormat.format(currDate);
    }

    private Driver createDriverWithDefauldData() {
        Driver driver = new Driver();
        driver.setId("1");
        driver.setDriverType(REGULAR_DRIVER_TYPE);
        driver.setAmountToPay(BigDecimal.valueOf(0));
        driver.setCurrencyType(PLN_CURRENCY_TYPE);
        driver.setLicensePlate(CORRECT_LICENCE_PLATE);
        driver.setStartTime(currentTime);
        driver.setTransactionDay(currentDate);

        return driver;
    }

    private DriverDTO createDriverDTOWithDefauldData() {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setDriverType(REGULAR_DRIVER_TYPE);
        driverDTO.setAmountToPay(BigDecimal.valueOf(0));
        driverDTO.setCurrencyType(PLN_CURRENCY_TYPE);
        driverDTO.setLicensePlate(CORRECT_LICENCE_PLATE);
        driverDTO.setStartTime(currentTime);
        driverDTO.setTransactionDay(currentDate);

        return driverDTO;
    }
}
