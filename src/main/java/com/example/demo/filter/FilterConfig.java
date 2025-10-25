package com.example.demo.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SimpleLoggingFilter> loggingFilter() {
        FilterRegistrationBean<SimpleLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SimpleLoggingFilter());
        registrationBean.addUrlPatterns("/api/*");  // apply only for /api/*
        registrationBean.setOrder(1);               // lower order = higher priority

        return registrationBean;
    }
}
