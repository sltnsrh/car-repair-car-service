package com.salatin.car.controller;

import com.salatin.car.model.dto.request.CarRequestDto;
import com.salatin.car.model.dto.response.CarResponseDto;
import com.salatin.car.service.CarService;
import com.salatin.car.service.mapper.CarMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @PreAuthorize(value = "hasRole('customer')")
    public Mono<CarResponseDto> create(@RequestBody @Valid CarRequestDto requestDto,
                            JwtAuthenticationToken authentication) {
        var car = carMapper.toModel(requestDto, authentication.getName());

        return carService.save(car)
            .map(carMapper::toDto);
    }

    @GetMapping
    @PreAuthorize(value = "hasAnyRole('admin', 'manager')")
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

    @GetMapping("/{id}")
    @PreAuthorize(value = "hasAnyRole('admin', 'manager', 'customer')")
    public Mono<CarResponseDto> findById(@PathVariable String id) {
        return carService.findById(id)
                .map(carMapper::toDto);
    }

    @GetMapping("/by-owner/{userId}")
    @PreAuthorize(value = "hasRole('manager')")
    public Flux<CarResponseDto> findAllByOwner(@PathVariable String userId) {

        return carService.findAllByUser(userId)
            .map(carMapper::toDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "hasAnyRole('customer', 'admin')")
    public Mono<CarResponseDto> update(@RequestBody @Valid CarRequestDto requestDto,
                                       @PathVariable String id,
                                       JwtAuthenticationToken authentication) {
        var car = carMapper.toModel(requestDto);

        return carService.update(car, id, authentication)
            .map(carMapper::toDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "hasAnyRole('customer', 'admin')")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id,
                             JwtAuthenticationToken authentication) {

        return carService.delete(id, authentication);
    }

    @GetMapping("/my-list")
    @PreAuthorize(value = "hasRole('customer')")
    public Flux<CarResponseDto> findMyCars(JwtAuthenticationToken authentication) {

        return carService.findAllByUser(authentication.getName())
            .map(carMapper::toDto);
    }
}
