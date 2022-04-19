package com.robotizac.fibonacciflux.controllers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@Component
public class SequenceSubscriber {

    public static final String HOST = "localhost";
    public static final int PORT = 3000;
    public static final String ROUTE = "give me first 50";
    public static final int MAX_BOUND = 50;
    private final RSocketRequester rsocketRequester;
    private final Random random = new Random();
    private final Cache<Tuple2<Integer, Integer>, Long> cache;

    public SequenceSubscriber(@Autowired RSocketRequester.Builder builder) {
        rsocketRequester = builder.tcp(HOST, PORT);
        cache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
    }

    @Scheduled(initialDelay = 1000, fixedRate = 7000)
    public void getFibonacci() {
        log.info("Received request to calculate sum");

        int beg = random.nextInt(MAX_BOUND);
        int size = MAX_BOUND - beg;

        Long sum = cache.getIfPresent(Tuples.of(beg, size));

        if (sum == null) {
            log.info("Requesting fibonacci");
            rsocketRequester
                    .route(ROUTE)
                    .retrieveFlux(Long.class)
                    .skip(beg)
                    .take(size)
                    .reduce(Long::sum)
                    .doOnNext(v -> cache.put(Tuples.of(beg, size), v))
                    .subscribe();
            try {
                Thread.sleep(100 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sum = cache.getIfPresent(Tuples.of(beg, size));
            log.info("Newly requested: {}", sum);
        } else {
            log.info("From cache: {}", sum);
        }
    }

}
