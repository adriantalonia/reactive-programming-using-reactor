package com.reactive.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.reactive.util.CommonUtil.delay;


@Slf4j
public class FluxAndMonoSchedulersService {
    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    public Flux<String> explore_publishOn() {

        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                //.publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Name is : {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_subscribeOn() {
        var namesFlux = flux1(namesList)
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                //.subscribeOn(Schedulers.parallel())
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        var namesFlux1 = flux1(namesList1)
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                //.subscribeOn(Schedulers.parallel())
                .subscribeOn(Schedulers.boundedElastic())
                .map((s) -> {
                    log.info("Value of s after boundedElastic is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public ParallelFlux<String> exploreParallel() {

        var noOfCores = Runtime.getRuntime().availableProcessors();
        log.info("cores: {}", noOfCores);

        return Flux.fromIterable(namesList)
                //.publishOn(Schedulers.parallel())
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();
    }

    public Flux<String> exploreParallelUsingFlatMap() {

        return Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    public Flux<String> exploreParallelUsingFlatMap1() {

        var namesFLux = Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();

        var namesFLux1 = Flux.fromIterable(namesList1)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();

        return namesFLux.mergeWith(namesFLux1);
    }

    public Flux<String> exploreParallelUsingFlatMapSequential() {

        return Flux.fromIterable(namesList)
                .flatMapSequential(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    private Flux<String> flux1(List<String> namesList) {
        return Flux.fromIterable(namesList)
                .map(this::upperCase);
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }
}
