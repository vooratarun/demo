package com.example.demo.controllers;

import com.example.demo.services.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    // Inject the specific bean by name using @Qualifier
    public NotificationController(@Qualifier("smsService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notify")
    public String sendNotification() {
        notificationService.sendNotification("Hello from Spring Boot!");
        return "Notification sent!";
    }
}
