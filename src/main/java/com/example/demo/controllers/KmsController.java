package com.example.demo.controllers;

import com.example.demo.services.KmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.kms.model.KeyMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kms")
@Tag(name = "KMS Encryption", description = "AWS Key Management Service operations")
@Slf4j
public class KmsController {

    private final KmsService kmsService;

    public KmsController(KmsService kmsService) {
        this.kmsService = kmsService;
    }

    /**
     * Encrypt plaintext data
     */
    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt data", description = "Encrypt plaintext data using KMS")
    public ResponseEntity<Map<String, String>> encryptData(@RequestBody Map<String, String> request) {
        try {
            String plaintext = request.get("plaintext");
            if (plaintext == null || plaintext.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "plaintext is required"));
            }

            String encryptedData = kmsService.encrypt(plaintext);
            Map<String, String> response = new HashMap<>();
            response.put("encryptedData", encryptedData);
            response.put("status", "success");

            log.info("Data encrypted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Encryption failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Encryption failed: " + e.getMessage()));
        }
    }

    /**
     * Decrypt ciphertext data
     */
    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt data", description = "Decrypt ciphertext data using KMS")
    public ResponseEntity<Map<String, String>> decryptData(@RequestBody Map<String, String> request) {
        try {
            String encryptedData = request.get("encryptedData");
            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "encryptedData is required"));
            }

            String decryptedData = kmsService.decrypt(encryptedData);
            Map<String, String> response = new HashMap<>();
            response.put("decryptedData", decryptedData);
            response.put("status", "success");

            log.info("Data decrypted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Decryption failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Decryption failed: " + e.getMessage()));
        }
    }

    /**
     * Generate a new data key for envelope encryption
     */
    @PostMapping("/generate-data-key")
    @Operation(summary = "Generate data key", description = "Generate a new data key for envelope encryption")
    public ResponseEntity<KmsService.DataKeyResult> generateDataKey() {
        try {
            KmsService.DataKeyResult result = kmsService.generateDataKey();
            log.info("Data key generated successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Data key generation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new KMS key
     */
    @PostMapping("/create-key")
    @Operation(summary = "Create KMS key", description = "Create a new KMS key")
    public ResponseEntity<Map<String, String>> createKey(@RequestBody Map<String, String> request) {
        try {
            String description = request.getOrDefault("description", "Demo KMS key created via API");
            String keyId = kmsService.createKey(description);

            Map<String, String> response = new HashMap<>();
            response.put("keyId", keyId);
            response.put("description", description);
            response.put("status", "success");

            log.info("KMS key created: {}", keyId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Key creation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Key creation failed: " + e.getMessage()));
        }
    }

    /**
     * List all KMS keys
     */
    @GetMapping("/keys")
    @Operation(summary = "List KMS keys", description = "List all KMS keys in the account")
    public ResponseEntity<List<KeyMetadata>> listKeys() {
        try {
            List<KeyMetadata> keys = kmsService.listKeys();
            return ResponseEntity.ok(keys);

        } catch (Exception e) {
            log.error("Key listing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get key information
     */
    @GetMapping("/keys/{keyId}")
    @Operation(summary = "Get key info", description = "Get detailed information about a KMS key")
    public ResponseEntity<KeyMetadata> getKeyInfo(@PathVariable String keyId) {
        try {
            KeyMetadata keyMetadata = kmsService.describeKey(keyId);
            return ResponseEntity.ok(keyMetadata);

        } catch (Exception e) {
            log.error("Key description failed for key: {}", keyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Enable a KMS key
     */
    @PostMapping("/keys/{keyId}/enable")
    @Operation(summary = "Enable key", description = "Enable a disabled KMS key")
    public ResponseEntity<Map<String, String>> enableKey(@PathVariable String keyId) {
        try {
            kmsService.enableKey(keyId);
            Map<String, String> response = new HashMap<>();
            response.put("keyId", keyId);
            response.put("status", "enabled");
            response.put("message", "Key enabled successfully");

            log.info("Key enabled: {}", keyId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Key enable failed for key: {}", keyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Key enable failed: " + e.getMessage()));
        }
    }

    /**
     * Disable a KMS key
     */
    @PostMapping("/keys/{keyId}/disable")
    @Operation(summary = "Disable key", description = "Disable a KMS key")
    public ResponseEntity<Map<String, String>> disableKey(@PathVariable String keyId) {
        try {
            kmsService.disableKey(keyId);
            Map<String, String> response = new HashMap<>();
            response.put("keyId", keyId);
            response.put("status", "disabled");
            response.put("message", "Key disabled successfully");

            log.info("Key disabled: {}", keyId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Key disable failed for key: {}", keyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Key disable failed: " + e.getMessage()));
        }
    }

    /**
     * Schedule key deletion
     */
    @DeleteMapping("/keys/{keyId}")
    @Operation(summary = "Schedule key deletion", description = "Schedule deletion of a KMS key")
    public ResponseEntity<Map<String, String>> scheduleKeyDeletion(
            @PathVariable String keyId,
            @RequestParam(defaultValue = "30") Integer pendingWindowInDays) {
        try {
            kmsService.scheduleKeyDeletion(keyId, pendingWindowInDays);
            Map<String, String> response = new HashMap<>();
            response.put("keyId", keyId);
            response.put("status", "scheduled_for_deletion");
            response.put("pendingWindowInDays", pendingWindowInDays.toString());
            response.put("message", "Key deletion scheduled successfully");

            log.info("Key deletion scheduled: {}", keyId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Key deletion scheduling failed for key: {}", keyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Key deletion scheduling failed: " + e.getMessage()));
        }
    }

    /**
     * Test encryption/decryption round trip
     */
    @PostMapping("/test-round-trip")
    @Operation(summary = "Test encryption round trip", description = "Test encrypt/decrypt functionality with sample data")
    public ResponseEntity<Map<String, Object>> testRoundTrip(@RequestBody Map<String, String> request) {
        try {
            String testData = request.getOrDefault("testData", "Hello, KMS World! " + System.currentTimeMillis());

            // Encrypt
            String encrypted = kmsService.encrypt(testData);

            // Decrypt
            String decrypted = kmsService.decrypt(encrypted);

            // Verify
            boolean success = testData.equals(decrypted);

            Map<String, Object> response = new HashMap<>();
            response.put("originalData", testData);
            response.put("encryptedData", encrypted);
            response.put("decryptedData", decrypted);
            response.put("roundTripSuccess", success);
            response.put("status", success ? "success" : "failed");

            log.info("Round trip test {}", success ? "successful" : "failed");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Round trip test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Round trip test failed: " + e.getMessage()));
        }
    }
}

