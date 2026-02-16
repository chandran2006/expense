package com.finmate.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinMateAIApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinMateAIApplication.class, args);
    }
}

