package com.salatin.car.service.mapper;

import com.salatin.car.model.Car;
import com.salatin.car.model.dto.request.CarRegistrationRequestDto;
import com.salatin.car.model.dto.response.CarResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", expression = "java(ownerId)")
    Car toModel(CarRegistrationRequestDto dto, String ownerId);

    CarResponseDto toDto(Car car);
}
