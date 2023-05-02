package com.salatin.car.service;

import com.salatin.car.model.Car;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarService {

    Mono<Car> save(Car car);

    Mono<Car> findById(String id);

    Mono<Car> findByUser(String userId);

    Flux<Car> findAll();

    Mono<Void> delete(Car car);
}
