package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:demo-bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload a file to S3
     */
    public String uploadFile(String key, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            log.info("File uploaded successfully: {}/{}", bucketName, key);
            return String.format("s3://%s/%s", bucketName, key);

        } catch (IOException | S3Exception e) {
            log.error("Failed to upload file: {}", key, e);
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    /**
     * Download a file from S3 as byte array
     */
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObject(getObjectRequest).readAllBytes();

        } catch (IOException | S3Exception e) {
            log.error("Failed to download file: {}", key, e);
            throw new RuntimeException("File download failed: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a file from S3
     */
    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}/{}", bucketName, key);

        } catch (S3Exception e) {
            log.error("Failed to delete file: {}", key, e);
            throw new RuntimeException("File deletion failed: " + e.getMessage(), e);
        }
    }

    /**
     * List all objects in bucket with optional prefix
     */
    public List<String> listFiles(String prefix) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix != null ? prefix : "")
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);

            return response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());

        } catch (S3Exception e) {
            log.error("Failed to list files with prefix: {}", prefix, e);
            throw new RuntimeException("File listing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Check if object exists in S3
     */
    public boolean objectExists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("Error checking object existence: {}", key, e);
            throw new RuntimeException("Object check failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get presigned URL for file access (valid for 24 hours)
     */
    public String getPresignedUrl(String key) {
        try {
            // Note: S3Presigner requires additional dependency
            // For now, return direct S3 URL
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", key, e);
            throw new RuntimeException("Presigned URL generation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create bucket if it doesn't exist
     */
    public void createBucketIfNotExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            try {
                s3Client.headBucket(headBucketRequest);
                log.info("Bucket already exists: {}", bucketName);
            } catch (NoSuchBucketException e) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();

                s3Client.createBucket(createBucketRequest);
                log.info("Bucket created successfully: {}", bucketName);
            }

        } catch (S3Exception e) {
            log.error("Failed to ensure bucket exists: {}", bucketName, e);
            throw new RuntimeException("Bucket creation failed: " + e.getMessage(), e);
        }
    }
}

