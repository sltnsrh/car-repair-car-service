package com.salatin.car.service.impl;

import com.salatin.car.model.Car;
import com.salatin.car.repository.CarRepository;
import com.salatin.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Override
    public Mono<Car> save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Mono<Car> findById(String id) {
        return carRepository.findById(id);
    }

    @Override
    public Flux<Car> findAll(PageRequest pageRequest) {
        int firstElement = pageRequest.getPageNumber() * pageRequest.getPageSize();
        int lastElement = firstElement + pageRequest.getPageSize();

        return carRepository.findAll(pageRequest.getSort())
            .limitRate(firstElement, lastElement)
            .log();
    }

    @Override
    public Mono<Void> delete(Car car) {
        return carRepository.delete(car);
    }
}
