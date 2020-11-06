package com.chelab.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerHandler {


    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${kafka.topic.group-id}")
    public void listen01(String message) {
        System.out.println("Received Message in listen01: " + message);
    }

}
