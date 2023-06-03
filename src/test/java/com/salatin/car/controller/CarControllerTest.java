package com.salatin.car.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.salatin.car.model.Car;
import com.salatin.car.model.dto.request.CarRequestDto;
import com.salatin.car.model.dto.response.CarResponseDto;
import com.salatin.car.service.CarService;
import com.salatin.car.service.mapper.CarMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {
    private static final String USER_ID = "userId";
    private static final String CAR_ID = "carId";

    @InjectMocks
    private CarController carController;
    @Mock
    private CarService carService;
    @Mock
    private CarMapper carMapper;
    @Mock
    private JwtAuthenticationToken authenticationToken;

    private CarRequestDto carRequestDto;
    private Car car;
    private CarResponseDto carResponseDto;

    @BeforeEach
    void init() {
        carRequestDto = new CarRequestDto();
        car = new Car();
        carResponseDto = new CarResponseDto();
    }

    @Test
    void createWhenValidRequestThenReturnCreatedCar() {
        when(carMapper.toModel(carRequestDto, USER_ID)).thenReturn(car);
        when(carService.save(car)).thenReturn(Mono.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);
        when(authenticationToken.getName()).thenReturn(USER_ID);

        StepVerifier.create(carController.create(carRequestDto, authenticationToken))
                .expectNext(carResponseDto)
                .verifyComplete();

        verify(carMapper).toModel(carRequestDto, USER_ID);
        verify(carService).save(car);
        verify(carMapper).toDto(car);
    }

    @Test
    void findAllWhenValidRequestThenReturnOneCarResponseDto() {
        when(carService.findAll(any(PageRequest.class))).thenReturn(Flux.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        StepVerifier.create(carController.findAll(0, 5, "brand", "DESC"))
                .expectNextCount(1)
                .verifyComplete();

        verify(carService).findAll(any(PageRequest.class));
        verify(carMapper).toDto(car);
    }

    @Test
    void findAllByOwnerWhenValidRequestThenReturnOneCarResponseDto() {
        when(carService.findAllByUser(USER_ID)).thenReturn(Flux.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        StepVerifier.create(carController.findAllByOwner(USER_ID))
                .expectNextCount(1)
                .verifyComplete();

        verify(carService).findAllByUser(USER_ID);
        verify(carMapper).toDto(car);
    }

    @Test
    void updateWhenValidRequestThenReturnUpdatedCar() {
        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carService.update(car, CAR_ID, authenticationToken)).thenReturn(Mono.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        StepVerifier.create(carController.update(carRequestDto, CAR_ID, authenticationToken))
                .expectNext(carResponseDto)
                .verifyComplete();

        verify(carMapper).toModel(carRequestDto);
        verify(carService).update(car, CAR_ID, authenticationToken);
        verify(carMapper).toDto(car);
    }

    @Test
    void deleteWhenValidRequestThenReturnVoid() {
        when(carService.delete(CAR_ID, authenticationToken)).thenReturn(Mono.empty());

        StepVerifier.create(carController.delete(CAR_ID, authenticationToken))
                .verifyComplete();

        verify(carService).delete(CAR_ID, authenticationToken);
    }

    @Test
    void findMyCarsWhenValidRequestThenReturnOneCarResponseDto() {
        when(carService.findAllByUser(USER_ID)).thenReturn(Flux.just(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);
        when(authenticationToken.getName()).thenReturn(USER_ID);

        StepVerifier.create(carController.findMyCars(authenticationToken))
                .expectNext(carResponseDto)
                .verifyComplete();
        verify(carService).findAllByUser(USER_ID);
        verify(carMapper).toDto(car);
    }
}
