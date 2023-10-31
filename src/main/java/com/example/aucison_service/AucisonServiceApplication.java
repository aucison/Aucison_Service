package com.example.aucison_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AucisonServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AucisonServiceApplication.class, args);
    }

}
