package com.example.demo.controllers;

import com.example.demo.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @PostMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        redisService.saveValue(key, value);
        return "Saved!";
    }

    @GetMapping("/get")
    public Object get(@RequestParam String key) {
        return redisService.getValue(key);
    }
}
