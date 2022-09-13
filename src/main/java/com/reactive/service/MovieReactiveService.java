package com.reactive.service;

import com.reactive.domain.Movie;
import com.reactive.domain.Review;
import com.reactive.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;


    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfoFLux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFLux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono
                    .map(reviewsList -> new Movie(movieInfo, reviewsList)).log();
        }).onErrorMap((ex) -> {
            log.error("Exception is : ", ex);
            throw new MovieException(ex.getMessage());
        }).log();
    }

    public Mono<Movie> getMovieById(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        return movieInfoMono.zipWith(reviewList, (movieInfo, reviews) -> new Movie(movieInfo, reviews));
                /*.onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });*/
    }

    public Mono<Movie> getMovieByIdFlatMap(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);

        return movieInfoMono.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono
                    .map(reviewsList -> new Movie(movieInfo, reviewsList));
        });
    }
}
