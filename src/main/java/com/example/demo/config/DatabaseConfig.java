package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseConfig {

    @Bean
    @Profile("dev")
    public String devDatabase() {
        return "Connected to DEV database (H2)";
    }

    @Bean
    @Profile("prod")
    public String prodDatabase() {
        return "Connected to PROD database (MySQL)";
    }

    @Bean
    @Profile({"dev", "prod"})
    public String commonBean() {
        return "This bean is loaded in both DEV and PROD profiles";
    }
}
