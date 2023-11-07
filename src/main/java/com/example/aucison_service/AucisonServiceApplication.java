package com.example.aucison_service;

import com.example.aucison_service.controller.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class AucisonServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public static void main(String[] args) {
        logger.info("main1");
        SpringApplication.run(AucisonServiceApplication.class, args);
        logger.info("main2");
    }

}
