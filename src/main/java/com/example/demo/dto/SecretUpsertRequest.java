package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class SecretUpsertRequest {

    @NotBlank(message = "secretName is required")
    private String secretName;

    private String secretString;

    private Map<String, String> secretMap;

    public String getSecretName() {
        return secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public String getSecretString() {
        return secretString;
    }

    public void setSecretString(String secretString) {
        this.secretString = secretString;
    }

    public Map<String, String> getSecretMap() {
        return secretMap;
    }

    public void setSecretMap(Map<String, String> secretMap) {
        this.secretMap = secretMap;
    }
}
