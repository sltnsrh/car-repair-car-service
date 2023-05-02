package com.salatin.car.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cars")
public class Car {
    @Id
    private String id;
    private String brand;
    private String model;
    private String licencePlate;
    private Short productionYear;
    private String vin;
}
