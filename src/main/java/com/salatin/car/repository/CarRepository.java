package com.salatin.car.repository;

import com.salatin.car.model.Car;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CarRepository extends ReactiveMongoRepository<Car, String> {

    @Query("{'ownerId': ?0}")
    Flux<Car> findByOwnerId(String ownerId);
}
