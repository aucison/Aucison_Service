package com.example.aucison_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing  //이거 활성화 해야 시간 자동...
@SpringBootApplication
public class AucisonServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(AucisonServiceApplication.class);

    public static void main(String[] args) {
        logger.info("main1");
        SpringApplication.run(AucisonServiceApplication.class, args);
        logger.info("main2");
    }

}
