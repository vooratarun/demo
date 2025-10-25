package com.example.demo.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("➡️ Interceptor PreHandle Incoming request: " + request.getMethod() + " " + request.getRequestURI());
        return true; // continue to controller
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("➡️ Interceptor postHandle Incoming request: " + request.getMethod() + " " + request.getRequestURI());
        return ; // continue to controlle
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        System.out.println("⬅️  Interceptor afterCompletion Response status: " + response.getStatus());
    }
}
