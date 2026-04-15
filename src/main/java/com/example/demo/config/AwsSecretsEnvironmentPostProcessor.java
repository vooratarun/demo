package com.example.demo.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class AwsSecretsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "awsSecretsManager";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("🔥 ENV PROCESSOR STARTED");

//        boolean enabled = Boolean.parseBoolean(environment.getProperty("aws.secretsmanager.enabled", "false"));
//        if (!enabled) {
//            return;
//        }

        System.out.println("here here");


        String secretName = environment.getProperty("aws.secretsmanager.secret-name");
        String region = environment.getProperty("aws.secretsmanager.region", "us-east-1");
        String endpoint = environment.getProperty("aws.secretsmanager.endpoint");
        boolean failFast = Boolean.parseBoolean(environment.getProperty("aws.secretsmanager.fail-fast", "false"));

        if (secretName == null || secretName.isBlank()) {
            log.warn("AWS Secrets Manager is enabled but 'aws.secretsmanager.secret-name' is not set");
            System.out.println("AWS Secrets Manager secret name is not set");

            return;
        }

        try {
            SecretsManagerClientBuilder builder = SecretsManagerClient.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create());
            if (endpoint != null && !endpoint.isBlank()) {
                builder.endpointOverride(URI.create(endpoint));
            }

            try (SecretsManagerClient client = builder.build()) {

                String secretString = client.getSecretValue(GetSecretValueRequest.builder()
                                .secretId(secretName)
                                .build())
                        .secretString();

                if (secretString == null || secretString.isBlank()) {
                    log.warn("Secret '{}' is empty", secretName);
                    System.out.println("Secret '{}' is empty");
                    return;
                }

                Map<String, Object> secretProperties = objectMapper.readValue(
                        secretString, new TypeReference<Map<String, Object>>() {});

                Map<String, Object> normalizedProperties = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : secretProperties.entrySet()) {
                    if (entry.getValue() != null) {
                        normalizedProperties.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }

                environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, normalizedProperties));
                log.info("Loaded {} properties from AWS Secrets Manager secret '{}'", normalizedProperties.size(), secretName);
                System.out.println("loaded proper"+ normalizedProperties.size() + secretName);

                System.out.println(normalizedProperties);


            }
        } catch (Exception ex) {
            String message = "Failed to load secrets from AWS Secrets Manager secret '" + secretName + "'";
            System.out.println(message);
            if (failFast) {
                throw new IllegalStateException(message, ex);
            }
            if (ex instanceof SdkException) {
                log.error(message, ex);
                System.out.println("error" + message);
                return;
            }
            log.error(message, ex);
            System.out.println("error" + message);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
