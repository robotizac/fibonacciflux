package com.robotizac.fibonacciflux.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Controller
public class SequencePublisher {

    public static final String ROUTE = "give me first 50";

    @MessageMapping(ROUTE)
    public Flux<Long> generateFirstFiftyOfFibonacci() {
        log.info("Generating first 50 of Fibonacci sequence: at {}", Instant.now());
        return Flux.generate(
                () -> Tuples.of(1L, 1L),
                (state, sink) -> {
                    sink.next(state.getT1());
                    return Tuples.of(state.getT2(), state.getT2() + state.getT1());
                }
        )
                .delayElements(Duration.ofMillis(100))
                .map(v -> (Long) v)
                .take(50);
    }

}
