package com.example.demo.controllers;


import com.example.demo.config.AppConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {


    private final AppConfig appConfig;


    public DemoController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping
    public String getStudents(){
        return "Hello world";
    }

    @GetMapping("/config")
    public String getConfig() {
        return "App: " + appConfig.getAppName()
                + ", Version: " + appConfig.getAppVersion()
                + ", Max Users: " + appConfig.getMaxUsers();
    }
}
