package com.example.demo.controllers;

import com.example.demo.dto.SecretUpsertRequest;
import com.example.demo.services.AwsSecretsManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secrets")
public class SecretManagerController {

    private final AwsSecretsManagerService secretsManagerService;

    public SecretManagerController(AwsSecretsManagerService secretsManagerService) {
        this.secretsManagerService = secretsManagerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrUpdateSecret(@Valid @RequestBody SecretUpsertRequest request) {
        boolean hasString = request.getSecretString() != null && !request.getSecretString().isBlank();
        boolean hasMap = request.getSecretMap() != null && !request.getSecretMap().isEmpty();

        if (hasString == hasMap) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Provide exactly one of secretString or secretMap"));
        }

        String secretValue = hasString
                ? request.getSecretString()
                : secretsManagerService.toJsonSecret(request.getSecretMap());

        try {
            secretsManagerService.createOrUpdateSecret(request.getSecretName(), secretValue);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Secret stored successfully",
                    "secretName", request.getSecretName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", ex.getMessage()));
        } catch (RuntimeException ex) {
            System.out.println("Failed to create secret store due to %s%n");
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to store secret"));
        }
    }
}
