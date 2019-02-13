package com.zaleskix.parking.mappers;


import com.zaleskix.parking.domain.DayProfit;
import com.zaleskix.parking.models.DayProfitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface DayProfitMapper {

    DayProfitMapper INSTANCE = Mappers.getMapper(DayProfitMapper.class);

    DayProfitDTO dayProfitToDayProfitDTO(DayProfit dayProfit);

    DayProfit dayProfitDTOToDayProfit(DayProfitDTO dayProfitDTO);
}