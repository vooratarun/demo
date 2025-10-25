package com.example.demo.services;

import org.springframework.stereotype.Service;

@Service("smsService")
public class SMSNotificationService implements NotificationService {

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending SMS: " + message);
    }
}
