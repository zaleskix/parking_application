package com.zaleskix.parking.mappers;


import com.zaleskix.parking.domain.CurrencyType;
import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.domain.DriverType;
import com.zaleskix.parking.models.DriverDTO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DriverMapperTest {

    public static final CurrencyType CURRENCY = CurrencyType.PLN;
    public static final DriverType DRIVER_TYPE = DriverType.VIP;
    DriverMapper driverMapper = DriverMapper.INSTANCE;

    @Test
    public void testDriverToDriverDTO() throws Exception {
        Driver driver = new Driver();
        driver.setCurrencyType(CURRENCY);
        driver.setDriverType(DRIVER_TYPE);

        DriverDTO driverDTO = driverMapper.driverToDriverDTO(driver);

        assertEquals(CURRENCY, driverDTO.getCurrencyType());
        assertEquals(DRIVER_TYPE, driverDTO.getDriverType());

    }

    @Test
    public void testDriverDTOToDriver() throws Exception {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setCurrencyType(CURRENCY);
        driverDTO.setDriverType(DRIVER_TYPE);

        Driver driver = driverMapper.driverDTOToDriver(driverDTO);

        assertEquals(CURRENCY, driver.getCurrencyType());
        assertEquals(DRIVER_TYPE, driver.getDriverType());

    }
}