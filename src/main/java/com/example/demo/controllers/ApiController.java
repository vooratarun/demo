package com.example.demo.controllers;

import com.example.demo.services.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/joke")
    public String getJoke() {
        log.info("Fetching user info");
        log.debug("User details fetched successfully");
        return apiService.getJoke();
    }
}
