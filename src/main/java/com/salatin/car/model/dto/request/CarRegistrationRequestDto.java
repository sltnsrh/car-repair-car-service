package com.salatin.car.model.dto.request;

import lombok.Data;

@Data
public class CarRegistrationRequestDto {
    private String brand;
    private String model;
    private String licencePlate;
    private Short productionYear;
    private String vin;
}
