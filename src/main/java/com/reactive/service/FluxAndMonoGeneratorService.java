package com.reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .log(); // could be db or remote service
    }

    public Mono<String> nameMono() {
        return Mono.just("adrian")
                .log();
    }

    public Flux<String> namesFluxMap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .log(); // could be db or remote service
    }

    public Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chole"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;

    }


    public Mono<String> nameMonoMapFilter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s))
                .log(); // could be db or remote service
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitStringWIthDelay(s))
                .log(); // could be db or remote service
    }

    public Flux<String> namesFluxConcatMap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(s -> splitStringWIthDelay(s))// preserve the order
                .log(); // could be db or remote service
    }

    public Mono<List<String>> nameMonoFlatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> nameMonoFlatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)// return flux when use Mono
                .log();
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .transform(filtermap)
                .flatMap(s -> splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxTransformSwitchEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filtermap);

        return Flux.fromIterable(List.of("alex", "ben", "chole"))
                .transform(filtermap)
                .switchIfEmpty(defaultFlux)
                .log();
    }


    public Mono<String> nameMonoMapFilterDefaultIfEmpty(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> nameMonoMapFilterSwitchIfEmpty(int stringLength) {

        var defaultMono = Mono.just("default");

        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .switchIfEmpty(defaultMono)
                .log();
    }


    public Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }


    public Flux<String> splitStringWIthDelay(String name) {
        var charArray = name.split("");
        //var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(1000));
    }


    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Flux name: " + name);
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name: " + name);
                });
    }

}
