package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    @Autowired
    private Environment env;

    @GetMapping("/env")
    public Map<String, Object> getEnv() {
        Map<String, Object> response = new HashMap<>();

        response.put("db.url", env.getProperty("spring.datasource.url"));
        response.put("db.username", env.getProperty("spring.datasource.username"));
        response.put("custom.value", env.getProperty("my.custom.key"));
        response.put("awsSecretsManager", env.getProperty("username"));

        return response;
    }
}