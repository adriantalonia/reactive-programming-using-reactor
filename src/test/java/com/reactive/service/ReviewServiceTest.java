package com.reactive.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    ReviewService reviewService = new ReviewService(webClient);

    @Test
    void retrieveAllReviews_RestClient() {
    }

    @Test
    void retrieveReviewsFlux_RestClient() {
        //given
        Long movieInfoId = 1L;
        //when
        var movieInfoFlux = reviewService.retrieveReviewsFlux_RestClient(movieInfoId);

        //then
        StepVerifier.create(movieInfoFlux)
                //.expectNextCount(1)
                .assertNext(movieInfo ->
                        assertEquals("Nolan is the real superhero", movieInfo.getComment())

                )
                .verifyComplete();
    }
}