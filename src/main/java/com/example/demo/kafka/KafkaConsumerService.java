package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(
            topics = "${app.kafka.topic.messages:demo-topic}",
            groupId = "${app.kafka.group.messages:demo-message-consumer-group}")
    public void consumeMessages(String message) {
        LOGGER.info("Consumer-1 received message: {}", message);
    }

    @KafkaListener(
            topics = "${app.kafka.topic.messages:demo-topic}",
            groupId = "${app.kafka.group.audit:demo-audit-consumer-group}")
    public void consumeMessagesForAudit(String message) {
        LOGGER.info("Consumer-2 (audit) observed message: {}", message);
    }

    @KafkaListener(
            topics = "${app.kafka.topic.notifications:demo-notification-topic}",
            groupId = "${app.kafka.group.notifications:demo-notification-consumer-group}")
    public void consumeNotifications(String message) {
        LOGGER.info("Consumer-3 received notification: {}", message);
    }
}
