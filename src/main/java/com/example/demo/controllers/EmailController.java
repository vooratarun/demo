package com.example.demo.controllers;

import com.example.demo.services.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-email")
    public String sendEmail(@RequestParam String recipient) {
        emailService.sendEmail(recipient, "Hello from Spring Boot!");
        return "Email request submitted!";  // Returns immediately without waiting
    }

    @GetMapping("/send-email-result")
    public CompletableFuture<String> sendEmailWithResult(@RequestParam String recipient) {
        return emailService.sendEmailWithResult(recipient, "Hello from Spring Boot!");
    }
}
