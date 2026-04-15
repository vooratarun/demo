package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KmsService {

    private final KmsClient kmsClient;

    @Value("${aws.kms.key-id:alias/demo-key}")
    private String keyId;

    public KmsService(KmsClient kmsClient) {
        this.kmsClient = kmsClient;
    }

    /**
     * Encrypt plaintext data using KMS
     */
    public String encrypt(String plaintext) {
        try {
            EncryptRequest encryptRequest = EncryptRequest.builder()
                    .keyId(keyId)
                    .plaintext(SdkBytes.fromUtf8String(plaintext))
                    .build();

            EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
            byte[] encryptedData = encryptResponse.ciphertextBlob().asByteArray();

            // Return Base64 encoded ciphertext for easy storage/transmission
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedData);

            log.info("Data encrypted successfully using key: {}", keyId);
            return encryptedBase64;

        } catch (KmsException e) {
            log.error("Failed to encrypt data with key: {}", keyId, e);
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt ciphertext data using KMS
     */
    public String decrypt(String encryptedBase64) {
        try {
            // Decode Base64 ciphertext
            byte[] encryptedData = Base64.getDecoder().decode(encryptedBase64);

            DecryptRequest decryptRequest = DecryptRequest.builder()
                    .ciphertextBlob(SdkBytes.fromByteArray(encryptedData))
                    .build();

            DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
            String plaintext = decryptResponse.plaintext().asUtf8String();

            log.info("Data decrypted successfully");
            return plaintext;

        } catch (KmsException e) {
            log.error("Failed to decrypt data", e);
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a new data key for envelope encryption
     */
    public DataKeyResult generateDataKey() {
        try {
            GenerateDataKeyRequest request = GenerateDataKeyRequest.builder()
                    .keyId(keyId)
                    .keySpec(DataKeySpec.AES_256)
                    .build();

            GenerateDataKeyResponse response = kmsClient.generateDataKey(request);

            DataKeyResult result = new DataKeyResult();
            result.setPlaintextKey(Base64.getEncoder().encodeToString(
                    response.plaintext().asByteArray()));
            result.setEncryptedKey(Base64.getEncoder().encodeToString(
                    response.ciphertextBlob().asByteArray()));
            result.setKeyId(response.keyId());

            log.info("Data key generated successfully with key: {}", keyId);
            return result;

        } catch (KmsException e) {
            log.error("Failed to generate data key with key: {}", keyId, e);
            throw new RuntimeException("Data key generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new KMS key
     */
    public String createKey(String description) {
        try {
            CreateKeyRequest createKeyRequest = CreateKeyRequest.builder()
                    .description(description)
                    .keyUsage(KeyUsageType.ENCRYPT_DECRYPT)
                    .keySpec(KeySpec.SYMMETRIC_DEFAULT)
                    .build();

            CreateKeyResponse createKeyResponse = kmsClient.createKey(createKeyRequest);
            String keyId = createKeyResponse.keyMetadata().keyId();

            log.info("KMS key created successfully: {}", keyId);
            return keyId;

        } catch (KmsException e) {
            log.error("Failed to create KMS key", e);
            throw new RuntimeException("Key creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * List KMS keys
     */
    public List<KeyMetadata> listKeys() {
        try {
            ListKeysRequest listKeysRequest = ListKeysRequest.builder().build();
            ListKeysResponse listKeysResponse = kmsClient.listKeys(listKeysRequest);

            return listKeysResponse.keys().stream()
                    .map(keyListEntry -> {
                        // Get full key metadata
                        DescribeKeyRequest describeRequest = DescribeKeyRequest.builder()
                                .keyId(keyListEntry.keyId())
                                .build();
                        return kmsClient.describeKey(describeRequest).keyMetadata();
                    })
                    .collect(Collectors.toList());

        } catch (KmsException e) {
            log.error("Failed to list KMS keys", e);
            throw new RuntimeException("Key listing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get key information
     */
    public KeyMetadata describeKey(String keyId) {
        try {
            DescribeKeyRequest describeKeyRequest = DescribeKeyRequest.builder()
                    .keyId(keyId)
                    .build();

            DescribeKeyResponse describeKeyResponse = kmsClient.describeKey(describeKeyRequest);
            return describeKeyResponse.keyMetadata();

        } catch (KmsException e) {
            log.error("Failed to describe key: {}", keyId, e);
            throw new RuntimeException("Key description failed: " + e.getMessage(), e);
        }
    }

    /**
     * Enable a KMS key
     */
    public void enableKey(String keyId) {
        try {
            EnableKeyRequest enableKeyRequest = EnableKeyRequest.builder()
                    .keyId(keyId)
                    .build();

            kmsClient.enableKey(enableKeyRequest);
            log.info("Key enabled successfully: {}", keyId);

        } catch (KmsException e) {
            log.error("Failed to enable key: {}", keyId, e);
            throw new RuntimeException("Key enable failed: " + e.getMessage(), e);
        }
    }

    /**
     * Disable a KMS key
     */
    public void disableKey(String keyId) {
        try {
            DisableKeyRequest disableKeyRequest = DisableKeyRequest.builder()
                    .keyId(keyId)
                    .build();

            kmsClient.disableKey(disableKeyRequest);
            log.info("Key disabled successfully: {}", keyId);

        } catch (KmsException e) {
            log.error("Failed to disable key: {}", keyId, e);
            throw new RuntimeException("Key disable failed: " + e.getMessage(), e);
        }
    }

    /**
     * Schedule key deletion (with 7-30 day waiting period)
     */
    public void scheduleKeyDeletion(String keyId, Integer pendingWindowInDays) {
        try {
            ScheduleKeyDeletionRequest request = ScheduleKeyDeletionRequest.builder()
                    .keyId(keyId)
                    .pendingWindowInDays(pendingWindowInDays != null ? pendingWindowInDays : 30)
                    .build();

            ScheduleKeyDeletionResponse response = kmsClient.scheduleKeyDeletion(request);
            log.info("Key deletion scheduled for: {} (deletion date: {})",
                    keyId, response.deletionDate());

        } catch (KmsException e) {
            log.error("Failed to schedule key deletion: {}", keyId, e);
            throw new RuntimeException("Key deletion scheduling failed: " + e.getMessage(), e);
        }
    }

    /**
     * Data class for data key results
     */
    public static class DataKeyResult {
        private String plaintextKey;
        private String encryptedKey;
        private String keyId;

        public String getPlaintextKey() { return plaintextKey; }
        public void setPlaintextKey(String plaintextKey) { this.plaintextKey = plaintextKey; }

        public String getEncryptedKey() { return encryptedKey; }
        public void setEncryptedKey(String encryptedKey) { this.encryptedKey = encryptedKey; }

        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = this.keyId; }
    }
}

