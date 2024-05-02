package com.example.aucison_service;

import com.example.aucison_service.controller.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableJpaAuditing  //이거 활성화 해야 시간 자동...
@SpringBootApplication
//빈 충돌문제 해결
@EnableJpaRepositories(basePackages = {"com.example.aucison_service.jpa.member.repository","com.example.aucison_service.jpa.product.repository","com.example.aucison_service.jpa.shipping.repository"})
@EnableElasticsearchRepositories("com.example.aucison_service.elastic")
@EnableAsync
@EnableScheduling   //스케줄링
public class AucisonServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public static void main(String[] args) {
        logger.info("main1");
        SpringApplication.run(AucisonServiceApplication.class, args);
        logger.info("main2");
    }

}
