package com.robotizac.fibonacciflux.controllers;

import java.time.Duration;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@Component
public class SequenceSubscriber {

    public static final String HOST = "localhost";
    public static final int PORT = 3000;
    public static final String ROUTE = "give me first 50";
    private final RSocketRequester rsocketRequester;
    private final Random random = new Random();

    public SequenceSubscriber(@Autowired RSocketRequester.Builder builder) {
        this.rsocketRequester = builder.tcp(HOST, PORT);
    }

    @Scheduled(initialDelay = 1000, fixedRate = 7000)
    public void getFibonacci() {
        log.info("Received request to calculate sum");

        this.rsocketRequester
            .route(ROUTE)
            .retrieveFlux(Long.class)
            .reduce(Long::sum)
            .doOnNext(System.out::println)
            .cache(Duration.ofSeconds(5))
            .subscribe();
    }

}
