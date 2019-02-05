package com.zaleskix.parking.services;




import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.domain.DriverType;
import com.zaleskix.parking.mappers.DriverMapper;
import com.zaleskix.parking.models.DriverDTO;
import com.zaleskix.parking.repositories.DriverRepository;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class DriverServiceImpl implements DriverService {

    private static String LICENCE_PLATE_PATTERN = "[A-Z]{2}-[0-9]{3}";
    private static String TIME_FORMAT = "HH:mm:ss";
    private static String DATE_FORMAT = "yyyy/MM/dd";
    
    private Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);

    private static final String BASE_URL = "/driver";
    private final DriverMapper driverMapper;
    private final DayProfitService dayProfitService;
    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverMapper driverMapper, DayProfitService dayProfitService, DriverRepository driverRepository) {
        this.driverMapper = driverMapper;
        this.dayProfitService = dayProfitService;
        this.driverRepository = driverRepository;
    }

    @Override
    public DriverDTO startParkingMeterByLicensePlate(String licensePlate) {

        if (! Pattern.matches(LICENCE_PLATE_PATTERN, licensePlate)) {
            logger.error("This is not valid licence plate");
            return null;
        }

        if ( driverRepository.findByLicensePlate(licensePlate).isPresent() ) {
            logger.info("Vehicle with given licence plate exist in database. Updating existing driver info ...");
            Driver driver = driverRepository.findByLicensePlate(licensePlate).get();
            driver.setAmountToPay(BigDecimal.valueOf(0));
            driver.setStopTime(null);
            return setStartTimeAndTransactionDayToGivenDriverAndSaveChangesInDatabase(driver);
        } else {
            logger.info("Vehicle with given licence plate not exist in database. Creating new Driver object ...");
            Driver driver = new Driver();
            driver.setLicensePlate(licensePlate);
            return setStartTimeAndTransactionDayToGivenDriverAndSaveChangesInDatabase(driver);
        }

    }


    @Override
    public boolean checkTicketIsValidByLicensePlate(String licensePlate){
        if ( driverRepository.findByLicensePlate(licensePlate).isPresent()) {
            Driver driver = driverRepository.findByLicensePlate(licensePlate).get();
            return driver.isTicketActive();
        } else {
            logger.error("Driver with given licence plate is not exist in database");
            return false;
        }
    }


    @Override
    public boolean checkTicketIsValidById(String id) {
        if ( driverRepository.findById(id).isPresent()) {
            Driver driver = driverRepository.findById(id).get();
            return driver.isTicketActive();
        } else {
            logger.error("Driver with given id is not exist in database");
            return false;
        }
    }

    @Override
    public DriverDTO stopParkingMeterById(String id) {

        DriverDTO returnedDriverDTO = driverRepository.findById(id)
                .map(this::stopParkingMeterAndCalculateAmountToPayForGivenDriver)
                .orElseThrow(ResourceNotFoundException::new);

        returnedDriverDTO.setDriverUrl(getDriverUrl(driverRepository.findById(id).get()));
        dayProfitService.saveOrUpdateDayProfitWithGivenDate(returnedDriverDTO.getTransactionDay());
        return returnedDriverDTO;
    }

    @Override
    public DriverDTO stopParkingMeterByLicensePlate(String licensePlate) {

        DriverDTO driverDTO = driverRepository.findByLicensePlate(licensePlate)
                .map(this::stopParkingMeterAndCalculateAmountToPayForGivenDriver)
                .orElseThrow(ResourceNotFoundException::new);

        driverDTO.setDriverUrl(getDriverUrl(driverRepository.findByLicensePlate(licensePlate).get()));
        dayProfitService.saveOrUpdateDayProfitWithGivenDate(driverDTO.getTransactionDay());
        return driverDTO;
    }

    @Override
    public BigDecimal checkAmountToPayById(String id) {
        DriverDTO driverDTO = findDriverByIdAndReturnDriverInfoAsDTO(id);
        return driverDTO.getAmountToPay();
    }

    @Override
    public BigDecimal checkAmountToPayByLicensePlate(String licensePlate) {
        DriverDTO driverDTO = findDriverByLicensePlateAndReturnDriverInfoAsDTO(licensePlate);
        return driverDTO.getAmountToPay();
    }

    @Override
    public DriverDTO findDriverByIdAndReturnDriverInfoAsDTO(String id) {

        return driverRepository.findById(id)
                .map(driverMapper::driverToDriverDTO)
                .map(driverDTOreturn -> {
                    driverDTOreturn.setDriverUrl(getDriverUrl(driverMapper.driverDTOToDriver(driverDTOreturn)));
                    return driverDTOreturn;
                })
                .orElseThrow(ResourceNotFoundException::new);

    }

    @Override
    public DriverDTO findDriverByLicensePlateAndReturnDriverInfoAsDTO(String licensePlate) {

        return driverRepository.findByLicensePlate(licensePlate)
                .map(driverMapper::driverToDriverDTO)
                .map(driverDTOreturn -> {
                    driverDTOreturn.setDriverUrl(getDriverUrl(driverMapper.driverDTOToDriver(driverDTOreturn)));
                    return driverDTOreturn;
                })
                .orElseThrow(ResourceNotFoundException::new);

    }

    //===========================
    // HELPERS METHODS
    //===========================

    private DriverDTO setStartTimeAndTransactionDayToGivenDriverAndSaveChangesInDatabase(Driver driver){
        driver.setTicketActive(true);

        String formattedTime = calculateCurrentDateAndReturnTimeAsString();
        driver.setStartTime(formattedTime);

        String formattedDate = calculateCurrentDateAndReturnTimeAsString();
        driver.setTransactionDay(formattedDate);

        return saveNewOrUpdateExistingDriverInDatabaseAndReturnDTO(driver);

    }
    private DriverDTO saveNewOrUpdateExistingDriverInDatabaseAndReturnDTO(Driver driver) {

        driver = driverRepository.save(driver);
        DriverDTO returnedDTO = driverMapper.driverToDriverDTO(driver);
        returnedDTO.setDriverUrl(getDriverUrl(driver));

        return returnedDTO;

    }

    private DriverDTO stopParkingMeterAndCalculateAmountToPayForGivenDriver(Driver driver){

        String formattedDate = calculateCurrentTimeAndReturnTimeAsString();

        driver.setStopTime(formattedDate);
        driver.setTicketActive(false);
        calculateAmountToPayForGivenDriver(driver);
        return driverMapper.driverToDriverDTO(driverRepository.save(driver));
    }

    private String calculateCurrentTimeAndReturnTimeAsString() {
        Date currDate = new Date();
        String strDateFormat = TIME_FORMAT;
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(currDate);
    }

    private String calculateCurrentDateAndReturnTimeAsString() {
        Date currDate = new Date();
        String strDateFormat = DATE_FORMAT;
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(currDate);
    }

    private void calculateAmountToPayForGivenDriver(Driver driver) {
        calculateAmountToPayForDriverWithGivenIdAndCurrencyType(driver.getId(), driver.getCurrencyType());
    }

    private void calculateAmountToPayForDriverWithGivenIdAndCurrencyType(String id, CurrencyType currencyType) {
        DriverDTO driverDTO = findDriverByIdAndReturnDriverInfoAsDTO(id);

        BigDecimal amountToPay = new BigDecimal(0.00);
        long parkingTime = calculateParkingTimeForDriverWithGivenId(id);

        if (driverDTO.getDriverType() == DriverType.VIP) {
            if (parkingTime >= 2) {
                amountToPay = BigDecimal.valueOf((2 + 1.2 * 2 * (parkingTime - 2)));

            } else if (parkingTime >= 1) {
                amountToPay = BigDecimal.valueOf(2.0);
            }
        } else {
            if (parkingTime >= 2) {
                amountToPay = BigDecimal.valueOf((1 + 2 + 1.5 * 2 * (parkingTime - 2)));
            } else if (parkingTime >= 1) {
                amountToPay = BigDecimal.valueOf(3.0);
            } else {
                amountToPay = BigDecimal.valueOf(1.0);
            }
        }

        Money finalAmountToPay = Money.parse(currencyType.toString() + String.valueOf(amountToPay));
        driverRepository.findById(id).map(driver -> {
            driver.setAmountToPay(finalAmountToPay.getAmount());
            driver.setCurrencyType(currencyType);
            saveNewOrUpdateExistingDriverInDatabaseAndReturnDTO(driver);
            return driverDTO;
        }).orElseThrow(ResourceNotFoundException::new);
    }

    private long calculateParkingTimeForDriverWithGivenId(String id) {
        DriverDTO driverDTO = findDriverByIdAndReturnDriverInfoAsDTO(id);

        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);

        try {
            Date date1 = format.parse(driverDTO.getStartTime());
            Date date2 = format.parse(driverDTO.getStopTime());
            long diff = date2.getTime() - date1.getTime();
            if (diff < 0) {
                logger.error("Calculated parking time in not positive number");
            }
            return diff / (60 * 60 * 1000) % 24;
        } catch (Exception e) {
            logger.error("Calculate parking time error: " + e.getMessage());
            return 0L;
        }

    }


    private String getDriverUrl(Driver driver) {
        return BASE_URL + "/" + driver.getId();
    }
}