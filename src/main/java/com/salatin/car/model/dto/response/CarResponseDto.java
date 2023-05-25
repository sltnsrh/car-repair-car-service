package com.salatin.car.model.dto.response;

import lombok.Data;

@Data
public class CarResponseDto {
    private String id;
    private String brand;
    private String model;
    private String licencePlate;
    private Short productionYear;
    private String vin;
    private String ownerId;
    private String createdAt;
    private String updatedAt;
}
