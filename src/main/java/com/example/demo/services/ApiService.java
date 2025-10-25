package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        return restTemplate.getForObject(url, String.class);
    }
}
