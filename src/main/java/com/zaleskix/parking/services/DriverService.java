package com.zaleskix.parking.services;

import com.zaleskix.parking.models.DriverDTO;

import java.math.BigDecimal;

public interface DriverService {

    DriverDTO startParkingMeterByLicensePlate(String licensePlate);

    DriverDTO stopParkingMeterById(String id);

    DriverDTO stopParkingMeterByLicensePlate(String licensePlate);

    boolean checkTicketIsValidByLicensePlate(String licensePlate);

    boolean checkTicketIsValidById(String id);

    DriverDTO findDriverByIdAndReturnDriverInfoAsDTO(String id);

    DriverDTO findDriverByLicensePlateAndReturnDriverInfoAsDTO(String licensePlate);

    BigDecimal checkAmountToPayById(String id);

    BigDecimal checkAmountToPayByLicensePlate(String licensePlate);



}