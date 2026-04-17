package com.example.demo.controllers;

import com.example.demo.dto.RazorpayCreateOrderRequest;
import com.example.demo.dto.RazorpayOrderResponse;
import com.example.demo.dto.RazorpayVerifyPaymentRequest;
import com.example.demo.services.RazorpayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/razorpay")
@Tag(name = "Razorpay", description = "Razorpay payment gateway integration")
@Slf4j
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @GetMapping("/key")
    @Operation(summary = "Get Razorpay key ID", description = "Returns the publishable Razorpay key for checkout")
    public ResponseEntity<Map<String, String>> getKey() {
        return ResponseEntity.ok(Map.of("keyId", razorpayService.getKeyId()));
    }

    @PostMapping("/orders")
    @Operation(summary = "Create Razorpay order", description = "Creates an order in Razorpay using amount in smallest currency unit")
    public ResponseEntity<?> createOrder(@RequestBody RazorpayCreateOrderRequest request) {
        try {
            RazorpayOrderResponse order = razorpayService.createOrder(request);
            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("keyId", razorpayService.getKeyId());
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create Razorpay order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Order creation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-payment")
    @Operation(summary = "Verify payment signature", description = "Verifies Razorpay payment signature from checkout response")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody RazorpayVerifyPaymentRequest request) {
        boolean valid = razorpayService.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );
        return ResponseEntity.ok(Map.of("valid", valid, "status", valid ? "verified" : "failed"));
    }

    @PostMapping("/verify-webhook")
    @Operation(summary = "Verify webhook signature", description = "Verifies Razorpay webhook body using X-Razorpay-Signature header")
    public ResponseEntity<Map<String, Object>> verifyWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        boolean valid = razorpayService.verifyWebhookSignature(payload, signature);
        return ResponseEntity.ok(Map.of("valid", valid, "status", valid ? "verified" : "failed"));
    }
}
