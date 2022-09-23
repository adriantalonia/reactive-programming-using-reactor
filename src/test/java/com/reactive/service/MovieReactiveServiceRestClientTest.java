package com.reactive.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    private MovieInfoService movieInfoService = new MovieInfoService(webClient);
    private ReviewService reviewService = new ReviewService(webClient);
    private RevenueService revenueService = new RevenueService();

    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies_RestClientTest() {
        //given

        //when
        var moviesFLux = movieReactiveService.getAllMovies_RestClient();
        //then
        StepVerifier.create(moviesFLux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieById_RestClient() {
        //given
        Long movieId = 1L;
        //when
        var movieMono = movieReactiveService.getMovieById_RestClient(movieId);
        //then
        StepVerifier.create(movieMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}
