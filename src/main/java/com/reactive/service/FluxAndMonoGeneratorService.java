package com.reactive.service;

import com.reactive.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.reactive.util.CommonUtil.delay;

@Slf4j
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
                .doOnNext(name -> {
                    System.out.println("Name is: " + name);
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription is: " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("Inside the complete callback");
                })
                .doFinally(signalType -> {
                    System.out.println("INside doFinally: " + signalType);
                })
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

    public Flux<String> exploreConcat() {
        var abdFLux = Flux.just("A", "B", "C");
        var defFLux = Flux.just("D", "E", "F");

        return Flux.concat(abdFLux, defFLux).log();
    }

    public Flux<String> exploreConcatWith() {
        var abdFLux = Flux.just("A", "B", "C");
        var defFLux = Flux.just("D", "E", "F");

        return abdFLux.concatWith(defFLux).log();
    }

    public Flux<String> exploreConcatWithMono() {
        var aFLux = Mono.just("A");
        var bFLux = Mono.just("B");

        return aFLux.concatWith(bFLux).log();
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    public Flux<String> exploreZipMap() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var flux3 = Flux.just("1", "2", "3");
        var flux4 = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, defFlux, flux3, flux4)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4()).log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux)
                .map(t2 -> t2.getT1() + t2.getT2()).log();
    }

    public Mono<String> exploreZipMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2()).log();
    }

    public Flux<String> exceptionFlux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"));
    }


    public Flux<String> exploreOnErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
                .onErrorReturn("D"); // default value
    }

    public Flux<String> exploreOnErrorResume(Exception e) {

        var recoverFlux = Flux.just("D", "E", "F");

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is: ", ex);
                    if (ex instanceof IllegalStateException)
                        return recoverFlux;
                    return Flux.error(ex);
                });
    }

    public Flux<String> exploreOnErrorContinue() {

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is: ", ex);
                    log.info("name is {}", name);
                });
    }

    public Flux<String> exploreOnErrorMap() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap((ex) -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    public Flux<String> exploreDoOnError() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
                .doOnError(ex -> {
                    log.error("Exception is: ", ex);

                });
    }

    public Mono<Object> exploreMonoOnErrorReturn() {
        return Mono.just("A")
                .map(value -> {
                    throw new RuntimeException("Exception occurred");
                })
                .onErrorReturn("abc");
    }

    public Mono<Object> exception_mono_onErrorMap(Exception e) {
        return Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception occurred");
                })
                .onErrorMap(ex -> {
                    System.out.println("Exception is: " + ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    public Mono<String> exception_mono_onErrorContinue(String input) {

        return Mono.just(input)
                .map(name -> {
                    if (name.equals("abc"))
                        throw new RuntimeException("Exception occurred");
                    return name;
                })
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is: ", ex);
                    log.info("name is {}", name);
                });
    }

    // Programmatically
    public Flux<Integer> exploreGenerate() {
        return Flux.generate(
                () -> 1, (state, sink) -> {
                    sink.next(state * 2);
                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                }
        );
    }

    public static List<String> names() {
        delay(100);
        return List.of("alex", "ben", "chloe");
    }

    public Flux<String> exploreCreate() {
        return Flux.create(sink -> {
            /*names()
                    .forEach(sink::next);*/
            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(sink::next);
                    })
                    .thenRun(() -> sendEvents(sink));
            //.thenRun(sink::complete);
            //sink.complete();
        });
    }

    public void sendEvents(FluxSink<String> sink) {
        CompletableFuture
                .supplyAsync(() -> names())
                .thenAccept(names -> {
                    names.forEach(sink::next);
                })
                .thenRun(sink::complete);
    }

    public Mono<String> exploreCreateMono() {
        return Mono.create(sink -> {
            sink.success("adrian");
        });
    }

    public Flux<String> exploreHandle() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name, sink) -> {
                    if (name.length() > 3) {
                       sink.next(name.toUpperCase());
                    }
                });
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
