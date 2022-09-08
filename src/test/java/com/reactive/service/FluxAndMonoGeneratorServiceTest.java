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
                .verifyComplete();
        ;
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

    @Test
    void nameMonoFlatMapMany() {
        //given
        int stringLength = 3;

        //when
        var value = fluxAndMonoGeneratorService.nameMonoFlatMapMany(stringLength);

        //then
        StepVerifier.create(value)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        //given
        int stringLength = 3;

        //given
        var names = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then
        StepVerifier.create(names)
                .expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform1() {
        //given
        int stringLength = 6;

        //given
        var names = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then
        StepVerifier.create(names)
                //.expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchEmpty() {

        //given
        int stringLength = 6;

        //given
        var names = fluxAndMonoGeneratorService.namesFluxTransformSwitchEmpty(stringLength);

        //then
        StepVerifier.create(names)
                //.expectNext("A", "L", "E", "X", "C", "H", "O", "L", "E")
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreConcat();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreConcatWith();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreConcatWithMono();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreMerge();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreMergeWith();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreMergeWithMono();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreMergeSequential();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreZip();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZipMap() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreZipMap();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipMono() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreZipMono();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        //given

        //when
        var concatFLux = fluxAndMonoGeneratorService.exploreZipWith();

        //then
        StepVerifier.create(concatFLux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exceptionFlux() {
        //given

        //when
        var value = fluxAndMonoGeneratorService.exceptionFlux().log();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exceptionFlux1() {
        //given

        //when
        var value = fluxAndMonoGeneratorService.exceptionFlux().log();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void exceptionFlux2() {
        //given

        //when
        var value = fluxAndMonoGeneratorService.exceptionFlux().log();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception occurred")
                .verify();
    }
}

