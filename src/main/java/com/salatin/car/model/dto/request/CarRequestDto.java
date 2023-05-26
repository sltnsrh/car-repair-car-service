package com.salatin.car.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Year;
import lombok.Data;

@Data
public class CarRequestDto {
    @NotBlank(message = "Brand can't be empty")
    private String brand;
    @NotBlank(message = "Model can't be empty")
    private String model;
    @NotBlank(message = "Licence plate can't be empty")
    @Size(min = 6)
    private String licencePlate;
    @Min(value = 1970) @Max(value = Year.MAX_VALUE)
    private Short productionYear;
    @NotBlank(message = "VIN number can't be empty")
    @Size(min = 17)
    private String vin;
}
