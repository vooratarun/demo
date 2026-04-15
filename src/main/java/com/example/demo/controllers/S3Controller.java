package com.example.demo.controllers;

import com.example.demo.services.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
@Tag(name = "S3 File Storage", description = "Manage files in AWS S3")
@Slf4j
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Upload a file to S3
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload file to S3", description = "Upload a file to the S3 bucket")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "key", required = false) String key) {
        try {
            String fileKey = key != null ? key : file.getOriginalFilename();
            String s3Url = s3Service.uploadFile(fileKey, file);
            log.info("File uploaded: {}", fileKey);
            return ResponseEntity.ok(s3Url);
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Download a file from S3
     */
    @GetMapping("/download/{key}")
    @Operation(summary = "Download file from S3", description = "Download a file from S3 by key")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
        try {
            byte[] fileContent = s3Service.downloadFile(key);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Download failed for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete a file from S3
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "Delete file from S3", description = "Delete a file from S3 by key")
    public ResponseEntity<String> deleteFile(@PathVariable String key) {
        try {
            s3Service.deleteFile(key);
            log.info("File deleted: {}", key);
            return ResponseEntity.ok("File deleted successfully: " + key);
        } catch (Exception e) {
            log.error("Deletion failed for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Deletion failed: " + e.getMessage());
        }
    }

    /**
     * List files in S3 bucket
     */
    @GetMapping("/list")
    @Operation(summary = "List files in S3", description = "List all files in bucket with optional prefix")
    public ResponseEntity<List<String>> listFiles(@RequestParam(value = "prefix", required = false) String prefix) {
        try {
            List<String> files = s3Service.listFiles(prefix);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("List failed with prefix: {}", prefix, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Check if file exists in S3
     */
    @GetMapping("/exists/{key}")
    @Operation(summary = "Check file existence", description = "Check if a file exists in S3")
    public ResponseEntity<Boolean> fileExists(@PathVariable String key) {
        try {
            boolean exists = s3Service.objectExists(key);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Existence check failed for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get presigned URL for file
     */
    @GetMapping("/presigned-url/{key}")
    @Operation(summary = "Get presigned URL", description = "Get a presigned URL for file access")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String key) {
        try {
            String url = s3Service.getPresignedUrl(key);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            log.error("Presigned URL generation failed for key: {}", key, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Presigned URL generation failed: " + e.getMessage());
        }
    }

    /**
     * Initialize bucket (create if not exists)
     */
    @PostMapping("/init-bucket")
    @Operation(summary = "Initialize S3 bucket", description = "Create bucket if it doesn't exist")
    public ResponseEntity<String> initBucket() {
        try {
            s3Service.createBucketIfNotExists();
            return ResponseEntity.ok("Bucket initialized successfully");
        } catch (Exception e) {
            log.error("Bucket initialization failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Bucket initialization failed: " + e.getMessage());
        }
    }
}

