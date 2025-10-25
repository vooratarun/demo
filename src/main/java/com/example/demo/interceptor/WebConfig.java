package com.example.demo.interceptor;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private PerformanceInterceptor performanceInterceptor;

    @Autowired
    private UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**")  // apply only to /api/*
                .excludePathPatterns("/api/auth/**").order(1); // exclude authentication paths

        registry.addInterceptor(performanceInterceptor).order(2);
        registry.addInterceptor(userContextInterceptor).order(3);
    }
}
