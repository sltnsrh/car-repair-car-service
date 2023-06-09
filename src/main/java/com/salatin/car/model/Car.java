package com.salatin.car.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed(unique = true)
    private String licencePlate;
    private Short productionYear;
    @Indexed(unique = true)
    private String vin;
    private String ownerId;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
