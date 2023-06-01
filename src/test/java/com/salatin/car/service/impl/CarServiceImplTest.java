package com.salatin.car.service.impl;

import com.salatin.car.model.Car;
import com.salatin.car.repository.CarRepository;
import com.salatin.car.service.mapper.CarMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    private static final String CAR_ID = "id";
    private static final String USER_ID = "userId";
    private static final String INVALID_USER_ID = "anotherUserId";

    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;
    private Car car;

    @BeforeEach
    void init() {
        car = new Car();
    }

    @Test
    void saveWhenSuccessThanReturnSavedCar() {
        when(carRepository.save(car))
                .thenReturn(Mono.just(car));

        StepVerifier.create(carService.save(car))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void saveWhenDuplicatedValuesThenResponseStatusException() {
        when(carRepository.save(car))
                .thenReturn(Mono.error(() -> new DuplicateKeyException("Duplicate key")));

        StepVerifier.create(carService.save(car))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode()
                        .equals(HttpStatus.CONFLICT))
                .verify();
    }

    @Test
    void updateWhenCarIdNotValidThenExpectedNotFoundError() {
        when(carService.findById(CAR_ID)).thenReturn(Mono.empty());

        StepVerifier.create(carService.update(car, CAR_ID, jwtAuthenticationToken))
                .expectErrorMatches(e ->
                        ((ResponseStatusException) e).getStatusCode()
                                .equals(HttpStatus.NOT_FOUND))
                .verify();
    }

    @Test
    void updateWhenUserIsNotOwnerOrAdminThenForbiddenError() {
        when(carService.findById(CAR_ID)).thenReturn(Mono.just(car));
        when(jwtAuthenticationToken.getName()).thenReturn(INVALID_USER_ID);

        StepVerifier.create(carService.update(car, CAR_ID, jwtAuthenticationToken))
                .expectErrorMatches(e ->
                        ((ResponseStatusException) e).getStatusCode()
                                .equals(HttpStatus.FORBIDDEN))
                .verify();
    }

    @Test
    void updateWhenUserIsOwnerThenReturnUpdatedCar() {
        var carFromDb = createCarWithIdAndOwnerId();

        when(carService.findById(CAR_ID)).thenReturn(Mono.just(carFromDb));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(carFromDb));
        when(jwtAuthenticationToken.getName()).thenReturn(USER_ID);

        StepVerifier.create(carService.update(car, CAR_ID, jwtAuthenticationToken))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByIdWhenValidIdThenReturnCar() {
        when(carRepository.findById(CAR_ID)).thenReturn(Mono.just(car));

        StepVerifier.create(carService.findById(CAR_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllWhenOneCarInDbThenExpectOneCar() {
        var pageRequest = createPageRequest();
        when(carRepository.findAll(pageRequest.getSort())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.findAll(pageRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteWhenCarIdInvalidThenNotFoundError() {
        when(carService.findById(CAR_ID)).thenReturn(Mono.empty());

        StepVerifier.create(carService.delete(CAR_ID, jwtAuthenticationToken))
                .expectErrorMatches(e ->
                        ((ResponseStatusException) e).getStatusCode()
                                .equals(HttpStatus.NOT_FOUND))
                .verify();
    }

    @Test
    void deleteWhenUserIsNotOwnerThenExpectedForbiddenError() {
        car.setOwnerId("otherOwnerId");
        when(carService.findById(CAR_ID)).thenReturn(Mono.just(car));
        when(jwtAuthenticationToken.getName()).thenReturn(INVALID_USER_ID);

        StepVerifier.create(carService.delete(CAR_ID, jwtAuthenticationToken))
                .expectErrorMatches(e -> ((ResponseStatusException) e).getStatusCode()
                        .equals(HttpStatus.FORBIDDEN))
                .verify();
    }

    @Test
    void deleteWhenCarExistsAndUserIsOwnerThenDeleteSuccess() {
        when(carService.findById(CAR_ID)).thenReturn(Mono.just(createCarWithIdAndOwnerId()));
        when(carRepository.delete(any())).thenReturn(Mono.empty());
        when(jwtAuthenticationToken.getName()).thenReturn(USER_ID);

        StepVerifier.create(carService.delete(CAR_ID, jwtAuthenticationToken))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteWhenUserIsAdminThenDeleteSuccess() {
        when(carService.findById(CAR_ID)).thenReturn(Mono.just(createCarWithIdAndOwnerId()));
        when(carRepository.delete(any())).thenReturn(Mono.empty());
        when(jwtAuthenticationToken.getName()).thenReturn("adminUserId");
        when(jwtAuthenticationToken.getAuthorities())
                .thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_admin")));

        StepVerifier.create(carService.delete(CAR_ID, jwtAuthenticationToken))
                .expectComplete()
                .verify();
    }

    @Test
    void findAllByUserWhenOwnerIdIsValidThenReturnCar() {
        when(carRepository.findByOwnerId(USER_ID)).thenReturn(Flux.just(car));

        StepVerifier.create(carService.findAllByUser(USER_ID))
                .expectNextCount(1)
                .verifyComplete();
    }

    private Car createCarWithIdAndOwnerId() {
        Car carFromDb = new Car();
        carFromDb.setId(CAR_ID);
        carFromDb.setOwnerId(USER_ID);
        return carFromDb;
    }

    private PageRequest createPageRequest() {
        return PageRequest.of(0, 5, Sort.Direction.ASC, "id");
    }
}
