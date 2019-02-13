package com.zaleskix.parking.services;

import com.zaleskix.parking.models.DriverDTO;

import java.math.BigDecimal;

public interface DriverService {

    DriverDTO startParkingMeterByLicensePlate(String licensePlate, String driverType, String currencyType);

    DriverDTO stopParkingMeterById(String id);

    DriverDTO stopParkingMeterByLicensePlate(String licensePlate);

    Boolean checkTicketIsValidByLicensePlate(String licensePlate);

    Boolean checkTicketIsValidById(String id);

    DriverDTO findDriverByIdAndReturnDriverInfoAsDTO(String id);

    DriverDTO findDriverByLicensePlateAndReturnDriverInfoAsDTO(String licensePlate);

    BigDecimal checkAmountToPayById(String id);

    BigDecimal checkAmountToPayByLicensePlate(String licensePlate);



}