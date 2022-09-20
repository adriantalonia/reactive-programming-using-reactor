package com.reactive.service;

import com.reactive.domain.Movie;
import com.reactive.domain.Review;
import com.reactive.exception.MovieException;
import com.reactive.exception.NetworkException;
import com.reactive.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;


    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
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

    public Flux<Movie> getAllMoviesRetry() {
        var moviesInfoFLux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFLux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono
                    .map(reviewsList -> new Movie(movieInfo, reviewsList)).log();
        }).onErrorMap((ex) -> {
            log.error("Exception is : ", ex);
            throw new MovieException(ex.getMessage());
        }).retry(3).log();
    }

    public Flux<Movie> getAllMoviesRetryWhen() {

        RetryBackoffSpec retryWhen = getRetryBackoffSpec();
        var moviesInfoFLux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFLux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            return reviewsMono
                    .map(reviewsList -> new Movie(movieInfo, reviewsList)).log();
        }).onErrorMap((ex) -> {
            log.error("Exception is : ", ex);
            if (ex instanceof NetworkException)
                throw new MovieException(ex.getMessage());
            else
                throw new ServiceException(ex.getMessage());
        }).retryWhen(retryWhen).log();
    }

    public Flux<Movie> getAllMoviesRepeat(long n) {

        RetryBackoffSpec retryWhen = getRetryBackoffSpec();
        var moviesInfoFLux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFLux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList)).log();
                }).onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                }).retryWhen(retryWhen)
                .repeat(n)
                .log();
    }


    private static RetryBackoffSpec getRetryBackoffSpec() {
        var retryWhen = Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));
        return retryWhen;
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

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono.zipWith(reviewList, (movieInfo, reviews) -> new Movie(movieInfo, reviews))
                .zipWith(revenueMono,(movie,revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                });
    }
}
