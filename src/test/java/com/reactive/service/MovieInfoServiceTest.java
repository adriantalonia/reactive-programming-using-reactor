package com.reactive.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void retrieveAllMovieInfo_RestClient() {
        //given

        //when
        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo_RestClient();

        //then
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();

    }

    @Test
    void retrieveMovieInfoById_RestClient() {
        //given
        Long movieId = 1L;
        //when
        var movieInfoFlux = movieInfoService.retrieveMovieInfoById_RestClient(movieId);

        //then
        StepVerifier.create(movieInfoFlux)
                //.expectNextCount(1)
                .assertNext(movieInfo ->
                        assertEquals("Batman Begins", movieInfo.getName())

                )
                .verifyComplete();

    }
}