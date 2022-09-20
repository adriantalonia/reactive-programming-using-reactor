package com.reactive.service;

import com.reactive.domain.Revenue;

import static com.reactive.util.CommonUtil.delay;

public class RevenueService {

    public Revenue getRevenue(Long movieId) {
        delay(1000); // simulating a network call ( DB or Rest call)
        return Revenue.builder()
                .movieInfoId(movieId)
                .budget(1000000)
                .boxOffice(5000000)
                .build();

    }
}
