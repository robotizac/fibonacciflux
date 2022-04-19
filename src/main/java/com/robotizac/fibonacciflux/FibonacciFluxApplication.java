package com.robotizac.fibonacciflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FibonacciFluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(FibonacciFluxApplication.class, args);
    }

}
