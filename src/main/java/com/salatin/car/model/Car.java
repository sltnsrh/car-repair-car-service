package com.salatin.car.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cars")
@Getter
@Setter
@ToString
public class Car {
    @Id
    @Indexed(unique = true)
    private String id;
    private String brand;
    private String model;
    private String licencePlate;
    private Short productionYear;
    private String vin;
    @Indexed(unique = true)
    private String ownerId;
}
