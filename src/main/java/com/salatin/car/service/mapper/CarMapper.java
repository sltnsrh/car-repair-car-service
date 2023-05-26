package com.salatin.car.service.mapper;

import com.salatin.car.model.Car;
import com.salatin.car.model.dto.request.CarRequestDto;
import com.salatin.car.model.dto.response.CarResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CarMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", expression = "java(ownerId)")
    Car toModel(CarRequestDto dto, String ownerId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    Car toModel(CarRequestDto dto);

    CarResponseDto toDto(Car car);

    void updateCarFromDb(Car car, @MappingTarget Car carToUpdate);
}
