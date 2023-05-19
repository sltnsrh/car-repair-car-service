package com.salatin.car.controller;

import com.salatin.car.model.Car;
import com.salatin.car.model.dto.request.CarRegistrationRequestDto;
import com.salatin.car.service.CarService;
import com.salatin.car.service.mapper.CarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final CarMapper carMapper;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<Car> create(@RequestBody CarRegistrationRequestDto requestDto,
                            JwtAuthenticationToken authentication) {
        var car = carMapper.toModel(requestDto, authentication.getName());

        return carService.save(car);
    }
}
