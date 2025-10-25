package com.example.demo.controllers;

import com.example.demo.services.GreetingService;
import com.example.demo.util.UtilityComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final GreetingService greetingService;
    private final UtilityComponent utilityComponent;

    public HelloController(GreetingService greetingService, UtilityComponent utilityComponent) {
        this.greetingService = greetingService;
        this.utilityComponent = utilityComponent;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return utilityComponent.getPrefix() + " " + greetingService.getGreeting(name);
    }
}
