package com.salatin.car.service.impl;

import com.salatin.car.model.Car;
import com.salatin.car.repository.CarRepository;
import com.salatin.car.service.CarService;
import com.salatin.car.service.mapper.CarMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Mono<Car> save(Car car) {
        return carRepository.save(car)
            .onErrorResume(DuplicateKeyException.class,
                ex -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                    "Can't create a new car", ex)));
    }

    @Override
    public Mono<Car> update(Car car, String id, JwtAuthenticationToken authentication) {
        return this.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Can't find a car with id " + id)))
            .flatMap(carFromDb -> {
                if (isOwnerOrAdmin(authentication, carFromDb)) {
                    carMapper.updateCarFromDb(car, carFromDb);
                    return this.save(carFromDb);
                }

                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized to update the car"));
            });
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
    public Mono<Void> delete(String id, JwtAuthenticationToken authentication) {
        return this.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Can't find a car with id " + id)))
            .flatMap(carFromDb -> {
                if (isOwnerOrAdmin(authentication, carFromDb)) {
                    return carRepository.delete(carFromDb);
                }

                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Unauthorized to delete the car"));
            });
    }

    @Override
    public Flux<Car> findAllByUser(String ownerId) {
        return carRepository.findByOwnerId(ownerId);
    }

    private boolean isOwnerOrAdmin(JwtAuthenticationToken authentication, Car carFromDb) {
        var hasRoleAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> "ROLE_admin".equals(a.getAuthority()));
        return authentication.getName().equals(carFromDb.getOwnerId())
            || hasRoleAdmin;
    }
}
