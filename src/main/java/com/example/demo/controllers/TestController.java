package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from API";
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        return "Received: " + body;
    }
}
