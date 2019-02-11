package com.zaleskix.parking.controllers;

import com.zaleskix.parking.models.DriverDTO;
import com.zaleskix.parking.services.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping(DriverController.BASE_URL)
public class DriverController {

    public static final String BASE_URL = "/driver";

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/start/{driverType}/{currencyType}/")
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDTO startParingkMeter(@RequestBody String licensePlate, @PathVariable String currencyType, @PathVariable String driverType) {
        return driverService.startParkingMeterByLicensePlate(licensePlate, driverType, currencyType);

    }

    @GetMapping({"/check/ID/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public boolean checkTicketValidByID(@PathVariable Long id) {
        return driverService.checkTicketIsValidById(String.valueOf(id));
    }

    @GetMapping({"/check/{licensePlate}/"})
    @ResponseStatus(HttpStatus.OK)
    public boolean checkTicketValidByLicencePlate(@PathVariable String licensePlate) {
        return driverService.checkTicketIsValidByLicensePlate(licensePlate);
    }


    @PutMapping({"/stop/ID/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public DriverDTO stopParkingMeterByID(@PathVariable Long id) {
        return driverService.stopParkingMeterById(String.valueOf(id));
    }

    @PutMapping({"/stop/{licensePlate}"})
    @ResponseStatus(HttpStatus.OK)
    public DriverDTO stopParkingMeterByLicensePlate(@PathVariable String licensePlate) {
        return driverService.stopParkingMeterByLicensePlate(licensePlate);
    }


    @GetMapping({"/cost/ID/{id}"})
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal checkAmoutToPayById(@PathVariable Long id) {
        return driverService.checkAmountToPayById(String.valueOf(id));
    }

    @GetMapping({"/cost/{licensePlate}"})
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal checkAmoutToPay(@PathVariable String licensePlate) {
        return driverService.checkAmountToPayByLicensePlate(licensePlate);
    }


    @GetMapping("/show/ID/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DriverDTO getDriverInfoById(@PathVariable Long id) {
        return driverService.findDriverByIdAndReturnDriverInfoAsDTO(String.valueOf(id));
    }

    @GetMapping("/show/{licensePlate}")
    @ResponseStatus(HttpStatus.OK)
    public DriverDTO getDriverInfoByLicensePlate(@PathVariable String licensePlate) {
        return driverService.findDriverByLicensePlateAndReturnDriverInfoAsDTO(licensePlate);
    }

}
