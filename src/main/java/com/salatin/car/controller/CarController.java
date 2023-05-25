package com.salatin.car.controller;

import com.salatin.car.model.dto.request.CarRegistrationRequestDto;
import com.salatin.car.model.dto.response.CarResponseDto;
import com.salatin.car.service.CarService;
import com.salatin.car.service.mapper.CarMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final CarMapper carMapper;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<CarResponseDto> create(@RequestBody @Valid CarRegistrationRequestDto requestDto,
                            JwtAuthenticationToken authentication) {
        var car = carMapper.toModel(requestDto, authentication.getName());

        return carService.save(car)
            .map(carMapper::toDto);
    }

    @GetMapping
    public Flux<CarResponseDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(defaultValue = "brand") String sortByField,
                                        @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest =
            PageRequest.of(page, size,
                Sort.Direction.valueOf(direction.toUpperCase()), sortByField);

        return carService.findAll(pageRequest)
            .map(carMapper::toDto);
    }
}
