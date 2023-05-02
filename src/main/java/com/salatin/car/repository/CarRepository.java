package com.salatin.car.repository;

import com.salatin.car.model.Car;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CarRepository extends ReactiveMongoRepository<Car, String> {

    Mono<Car> findByUserId(String userId);
}
