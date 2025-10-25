package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        return new RestTemplate();

    }



//    @Bean
//    public RestTemplate getRestTemplateIgnoringSSL() throws Exception {
//        // Create a trust-all SSL context
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(null, (certificate, authType) -> true)
//                .build();
//
//        HttpClient httpClient = HttpClients.custom()
//                .setSSLContext(sslContext)
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .build();
//
//        HttpComponentsClientHttpRequestFactory factory =
//                new HttpComponentsClientHttpRequestFactory(httpClient);
//
//        return new RestTemplate(factory);
//    }
}
