package com.example.aucison_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "productRegisteredTopic", groupId = "product_group")
    public void consumeProductRegisteredEvent(Map<String, Object> message) {
        System.out.println("Received Kafka message: " + message);
        // 메시지 처리 로직 (예: 검색 인덱스 업데이트, 이메일 알림 발송 등)
    }
}