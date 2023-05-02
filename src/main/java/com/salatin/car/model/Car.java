package com.salatin.car.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cars")
@Getter
@Setter
@ToString
public class Car {
    @Id
    private String id;
    private String brand;
    private String model;
    private String licencePlate;
    private Short productionYear;
    private String vin;
    private String userId;
}
