package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String messagesTopic;
    private final String notificationsTopic;

    public KafkaProducerService(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic.messages:demo-topic}") String messagesTopic,
            @Value("${app.kafka.topic.notifications:demo-notification-topic}") String notificationsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagesTopic = messagesTopic;
        this.notificationsTopic = notificationsTopic;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(messagesTopic, message);
        LOGGER.info("Sent message to topic '{}' : {}", messagesTopic, message);
    }

    public void sendNotification(String notification) {
        kafkaTemplate.send(notificationsTopic, notification);
        LOGGER.info("Sent notification to topic '{}' : {}", notificationsTopic, notification);
    }
}
