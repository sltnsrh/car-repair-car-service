package com.salatin.car.service.impl;

import com.salatin.car.model.Car;
import com.salatin.car.repository.CarRepository;
import com.salatin.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Override
    public Mono<Car> save(Car car) {
        return carRepository.save(car)
            .onErrorResume(DuplicateKeyException.class,
                ex -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                    "Can't create a new car", ex)));
    }

    @Override
    public Mono<Car> findById(String id) {
        return carRepository.findById(id);
    }

    @Override
    public Flux<Car> findAll(PageRequest pageRequest) {
        int firstElement = pageRequest.getPageNumber() * pageRequest.getPageSize();

        return carRepository.findAll(pageRequest.getSort())
            .skip(firstElement)
            .take(pageRequest.getPageSize())
            .log();
    }

    @Override
    public Mono<Void> delete(Car car) {
        return carRepository.delete(car);
    }
}
