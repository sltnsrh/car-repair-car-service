package com.salatin.car.service;

import com.salatin.car.model.Car;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarService {

    Mono<Car> save(Car car);

    Mono<Car> findById(String id);

    Flux<Car> findAll(PageRequest pageRequest);

    Mono<Void> delete(Car car);
}
