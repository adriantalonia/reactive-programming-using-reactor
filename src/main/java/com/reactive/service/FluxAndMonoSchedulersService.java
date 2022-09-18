package com.reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import static reactor.core.publisher.Mono.delay;

public class FluxAndMonoSchedulersService {
    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    public Flux<String> explore_publishOn() {

        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Name is : {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    private String upperCase(String name) {
        delay(Duration.ofDays(1000));
        return name.toUpperCase();
    }
}
