package com.example.demo.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.ResourceExistsException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class AwsSecretsManagerService {
    private static final Logger log = LoggerFactory.getLogger(AwsSecretsManagerService.class);

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.secretsmanager.secret-name:}")
    private String defaultSecretName;

    public AwsSecretsManagerService(
            @Autowired(required = false) SecretsManagerClient secretsManagerClient,
            ObjectMapper objectMapper) {
        this.secretsManagerClient = secretsManagerClient;
        this.objectMapper = objectMapper;
    }

    public Optional<String> getSecretString(String secretName) {
        if (secretsManagerClient == null || secretName == null || secretName.isBlank()) {
            return Optional.empty();
        }

        try {
            GetSecretValueResponse response = secretsManagerClient.getSecretValue(
                    GetSecretValueRequest.builder().secretId(secretName).build());
            return Optional.ofNullable(response.secretString());
        } catch (SdkException ex) {
            log.error("Failed to fetch secret '{}' from AWS Secrets Manager", secretName, ex);
            return Optional.empty();
        }
    }

    public Optional<String> getDefaultSecretString() {
        return getSecretString(defaultSecretName);
    }

    public Map<String, String> getSecretMap(String secretName) {
        return getSecretString(secretName)
                .map(secret -> {
                    try {
                        return objectMapper.readValue(secret, new TypeReference<Map<String, String>>() {});
                    } catch (Exception ex) {
                        log.error("Secret '{}' is not a valid JSON object", secretName, ex);
                        return Collections.<String, String>emptyMap();
                    }
                })
                .orElse(Collections.emptyMap());
    }

    public Map<String, String> getDefaultSecretMap() {
        return getSecretMap(defaultSecretName);
    }

    public void createOrUpdateSecret(String secretName, String secretValue) {
        if (secretsManagerClient == null) {
            throw new IllegalStateException("AWS Secrets Manager is disabled");
        }
        if (secretName == null || secretName.isBlank()) {
            throw new IllegalArgumentException("secretName is required");
        }
        if (secretValue == null || secretValue.isBlank()) {
            throw new IllegalArgumentException("secretValue is required");
        }

        try {
            secretsManagerClient.createSecret(CreateSecretRequest.builder()
                    .name(secretName)
                    .secretString(secretValue)
                    .build());
        } catch (ResourceExistsException ex) {
            log.error("Secret '{}' already exists", secretName, ex);
            secretsManagerClient.putSecretValue(PutSecretValueRequest.builder()
                    .secretId(secretName)
                    .secretString(secretValue)
                    .build());
        } catch (SdkException ex) {
            log.error("e: ", ex);
            throw new RuntimeException("Failed to create/update secret in AWS Secrets Manager", ex);
        }
    }

    public String toJsonSecret(Map<String, String> values) {
        try {
            return objectMapper.writeValueAsString(values);
        } catch (Exception ex) {
            throw new IllegalArgumentException("secretMap is not serializable", ex);
        }
    }
}
