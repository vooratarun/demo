package com.example.demo.controllers;

import com.example.demo.kafka.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducerService producer;

    public MessageController(KafkaProducerService producer) {
        this.producer = producer;
    }

    @PostMapping
    public String sendMessage(@RequestParam String message) {
        producer.sendMessage(message);
        return "Message sent to Kafka topic 'messages': " + message;
    }

    @PostMapping("/notifications")
    public String sendNotification(@RequestParam String message) {
        producer.sendNotification(message);
        return "Notification sent to Kafka topic 'notifications': " + message;
    }
}
