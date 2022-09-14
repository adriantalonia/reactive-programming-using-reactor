package com.reactive.service;

import com.reactive.exception.MovieException;
import com.reactive.exception.NetworkException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @Mock
    private MovieInfoService movieInfoService;
    @Mock
    private ReviewService reviewService;

    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Test
    void getALlMovies() {
        //given
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();
        //when
        var movieFlux = movieReactiveService.getAllMovies();

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void getALlMoviesThrowException() {
        //given
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException("Exception occurred in reviewService"));
        //when
        var movieFlux = movieReactiveService.getAllMovies();

        //then
        StepVerifier.create(movieFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage("Exception occurred in reviewService")
                .verify();

    }

    @Test
    void getAllMoviesRetry() {
        //given
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException("Exception occurred in reviewService"));
        //when
        var movieFlux = movieReactiveService.getAllMoviesRetry();

        //then
        StepVerifier.create(movieFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage("Exception occurred in reviewService")
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMoviesRetryWhen() {
        //given
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        /*Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException("Exception occurred in reviewService"));*/

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new NetworkException("Exception occurred in reviewService"));
        //when
        var movieFlux = movieReactiveService.getAllMoviesRetryWhen();

        //then
        StepVerifier.create(movieFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage("Exception occurred in reviewService")
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMoviesRepeat() {
        //given
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var numTimes = 2L;
        //when
        var movieFlux = movieReactiveService.getAllMoviesRepeat(numTimes);

        //then
        /*StepVerifier.create(movieFlux)
                .expectNextCount(9)
                .thenCancel()
                .verify();*/
        StepVerifier.create(movieFlux)
                .expectNextCount(9)
                .verifyComplete();

        verify(reviewService, times(9)).retrieveReviewsFlux(isA(Long.class));

    }


}