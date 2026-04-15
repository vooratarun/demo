package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

import java.net.URI;

@Configuration
public class KmsConfig {

    @Value("${aws.kms.region:us-east-1}")
    private String region;

    @Value("${aws.kms.access-key:test}")
    private String accessKey;

    @Value("${aws.kms.secret-key:test}")
    private String secretKey;

    @Value("${aws.kms.endpoint:#{null}}")
    private String endpoint;

    @Bean
    public KmsClient kmsClient() {
        KmsClientBuilder builder = KmsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ));

        // Configure endpoint for LocalStack or custom KMS endpoint
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }
}

