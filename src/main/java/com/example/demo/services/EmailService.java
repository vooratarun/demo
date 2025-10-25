package com.example.demo.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Async
    public void sendEmail(String recipient, String message) {
        try {
            System.out.println("Sending email to " + recipient + " in thread: " + Thread.currentThread().getName());
            Thread.sleep(5000);  // Simulate delay
            System.out.println("Email sent to " + recipient);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Async method with return type
    @Async
    public CompletableFuture<String> sendEmailWithResult(String recipient, String message) {
        try {
            System.out.println("Sending email to " + recipient + " in thread: " + Thread.currentThread().getName());
            Thread.sleep(3000);  // Simulate delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("Email sent to " + recipient);
    }
}
