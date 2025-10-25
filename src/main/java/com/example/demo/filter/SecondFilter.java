package com.example.demo.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;

import java.io.IOException;


@Component
@Order(2)
public class SecondFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("SecondFilter");
        chain.doFilter(request, response);
    }
}
