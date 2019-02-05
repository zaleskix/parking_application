package com.zaleskix.parking.mappers;

import com.zaleskix.parking.domain.Driver;
import com.zaleskix.parking.models.DriverDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel="spring")
public interface DriverMapper {

    DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    DriverDTO driverToDriverDTO(Driver driver);

    Driver driverDTOToDriver(DriverDTO driverDTO);

}