package com.salatin.car.service;

import com.salatin.car.model.Car;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarService {

    Mono<Car> save(Car car);

    Mono<Car> update(Car car, String id, JwtAuthenticationToken authentication);

    Mono<Car> findById(String id);

    Flux<Car> findAll(PageRequest pageRequest);

    Mono<Void> delete(String id, JwtAuthenticationToken authentication);

    Flux<Car> findAllByUser(String ownerId);
}
