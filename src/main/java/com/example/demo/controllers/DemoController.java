package com.example.demo.controllers;


import com.example.demo.config.AppConfig;
import com.example.demo.config.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);


    private final AppConfig appConfig;
    private final AppInfo appInfo;


    public DemoController(AppConfig appConfig, AppInfo appInfo) {
        this.appConfig = appConfig;
        this.appInfo = appInfo;
    }



    @GetMapping("/appinfo")
    public String getAppInfo() {
        logger.info("INFO: Hello endpoint called");
        logger.debug("DEBUG: Detailed debugging information");
        logger.warn("WARN: This is a warning message");
        logger.error("ERROR: Something went wrong");


        return appInfo.getAppDetails();
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
