package com.example.demo.services;

import com.example.demo.dto.RazorpayCreateOrderRequest;
import com.example.demo.dto.RazorpayOrderResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;

@Service
@Slf4j
public class RazorpayService {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private HttpClient httpClient;

    @Value("${razorpay.key-id:}")
    private String keyId;

    @Value("${razorpay.key-secret:}")
    private String keySecret;

    @Value("${razorpay.base-url:https://api.razorpay.com}")
    private String baseUrl;

    @Value("${razorpay.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${razorpay.read-timeout-ms:30000}")
    private int readTimeoutMs;

    public RazorpayService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
    }

    public RazorpayOrderResponse createOrder(RazorpayCreateOrderRequest request) {
        validateCreateOrderRequest(request);
        validateCredentials();

        try {
            String payload = objectMapper.writeValueAsString(request);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/orders"))
                    .timeout(Duration.ofMillis(readTimeoutMs))
                    .header("Authorization", "Basic " + encodedAuthToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < HttpStatus.OK.value() || response.statusCode() >= HttpStatus.MULTIPLE_CHOICES.value()) {
                log.error("Razorpay order creation failed. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new IllegalStateException("Razorpay order creation failed with status " + response.statusCode());
            }

            JsonNode json = objectMapper.readTree(response.body());
            RazorpayOrderResponse orderResponse = new RazorpayOrderResponse();
            orderResponse.setId(json.path("id").asText(null));
            orderResponse.setEntity(json.path("entity").asText(null));
            orderResponse.setAmount(json.path("amount").asLong());
            orderResponse.setAmountPaid(json.path("amount_paid").asLong());
            orderResponse.setAmountDue(json.path("amount_due").asLong());
            orderResponse.setCurrency(json.path("currency").asText(null));
            orderResponse.setReceipt(json.path("receipt").asText(null));
            orderResponse.setStatus(json.path("status").asText(null));
            orderResponse.setAttempts(json.path("attempts").asInt());
            orderResponse.setCreatedAt(json.path("created_at").asLong());
            return orderResponse;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Razorpay order", e);
        }
    }

    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        validateCredentials();
        if (isBlank(razorpayOrderId) || isBlank(razorpayPaymentId) || isBlank(razorpaySignature)) {
            return false;
        }
        String payload = razorpayOrderId + "|" + razorpayPaymentId;
        String expectedSignature = hmacHex(payload, keySecret);
        return constantTimeEquals(expectedSignature, razorpaySignature);
    }

    public boolean verifyWebhookSignature(String payload, String webhookSignature) {
        validateCredentials();
        if (isBlank(payload) || isBlank(webhookSignature)) {
            return false;
        }
        String expectedSignature = hmacHex(payload, keySecret);
        return constantTimeEquals(expectedSignature, webhookSignature);
    }

    public String getKeyId() {
        return keyId;
    }

    private void validateCreateOrderRequest(RazorpayCreateOrderRequest request) {
        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0 in smallest currency unit");
        }
        if (isBlank(request.getCurrency())) {
            request.setCurrency("INR");
        }
    }

    private void validateCredentials() {
        if (isBlank(keyId) || isBlank(keySecret)) {
            throw new IllegalStateException("Razorpay credentials are not configured");
        }
    }

    private String encodedAuthToken() {
        String token = keyId + ":" + keySecret;
        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacHex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Unable to compute HMAC signature", e);
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(
                left.getBytes(StandardCharsets.UTF_8),
                right.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
