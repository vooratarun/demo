//package com.example.demo.controllers;
//
//import com.example.demo.dto.SqsMessageRequest;
//import com.example.demo.services.SqsService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/sqs")
//public class SqsController {
//
////    private final SqsService sqsService;
////e
////    public SqsController(SqsService sqsService) {
////        this.sqsService = sqsService;
////    }
////
////    @PostMapping("/messages")
////    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody SqsMessageRequest request) {
////        sqsService.sendMessage(request.getMessage());
////        return ResponseEntity.ok(Map.of("status", "queued", "message", request.getMessage()));
////    }
//}
