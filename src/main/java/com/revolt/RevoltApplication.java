package com.revolt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RevoltApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevoltApplication.class, args);
    }
}
