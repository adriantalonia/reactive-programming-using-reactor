package com.reactive.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chole")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameMono() {
        //given

        //when
        var nameMono = fluxAndMonoGeneratorService.nameMono();

        //then
        StepVerifier.create(nameMono)
                .expectNext("adrian")
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHOLE")
                //.expectNext("ALEX", "CHOLE")
                .expectNext("4-ALEX", "5-CHOLE")
                .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxImmutability();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHOLE")//error
                .expectNext("alex", "ben", "chole")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilter() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.nameMonoMapFilter(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);
        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength);
        //then
        StepVerifier.create(namesFlux)
                //.expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .expectNextCount(9)
                .verifyComplete();;
    }

    @Test
    void namesFluxConcatMap() {
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(stringLength);
        //then
        StepVerifier.create(namesFlux)
                //.expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void nameMonoFlatMap() {
        //given
        int stringLength = 3;
        //when
        var mono = fluxAndMonoGeneratorService.nameMonoFlatMap(stringLength);
        //then
        StepVerifier.create(mono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }
}