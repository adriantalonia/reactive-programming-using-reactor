package com.reactive.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new
            FluxAndMonoSchedulersService();

    @Test
    void explore_publishOn() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.explore_publishOn().log();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subscribeOn() {
        //given

        //when
        var flux = fluxAndMonoSchedulersService.explore_subscribeOn().log();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallel() {
        //given
        //when
        var flux = fluxAndMonoSchedulersService.exploreParallel();

        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMap() {
        //given
        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMap();

        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMap1() {
        //given
        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMap1();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMapSequential() {
        //given
        //when
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMapSequential();

        //then
        StepVerifier.create(flux)
                //.expectNextCount(3)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}