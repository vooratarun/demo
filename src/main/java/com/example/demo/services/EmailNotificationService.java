package com.example.demo.services;

import org.springframework.stereotype.Service;

@Service("emailNotificationService")
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending EMAIL: " + message);
    }
}
