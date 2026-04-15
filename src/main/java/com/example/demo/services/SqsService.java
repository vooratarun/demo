package com.example.demo.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

@Service
public class SqsService {

    private static final Logger log = LoggerFactory.getLogger(SqsService.class);

    private final SqsClient sqsClient;
    private final String queueName;
    private String queueUrl;

    public SqsService(SqsClient sqsClient, @Value("${aws.sqs.queue-name}") String queueName) {
        this.sqsClient = sqsClient;
        this.queueName = queueName;
    }

    @PostConstruct
    public void initQueue() {
        this.queueUrl = resolveQueueUrl();
    }

    public void sendMessage(String message) {
        if (queueUrl == null || queueUrl.isBlank()) {
            queueUrl = resolveQueueUrl();
        }
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build());
    }

    @Scheduled(cron = "${aws.sqs.poll.cron:0 */1 * * * *}")
    public void processMessages() {
        if (queueUrl == null || queueUrl.isBlank()) {
            queueUrl = resolveQueueUrl();
        }

        int processed = 0;
        while (true) {
            List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(10)
                            .waitTimeSeconds(1)
                            .build())
                    .messages();

            if (messages == null || messages.isEmpty()) {
                break;
            }

            for (Message message : messages) {
                log.info("Processing SQS message id={} body={}", message.messageId(), message.body());
                sqsClient.deleteMessage(builder -> builder
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle()));
                processed++;
            }
        }

        if (processed > 0) {
            log.info("Processed {} message(s) from SQS queue {}", processed, queueName);
        }
    }

    private String resolveQueueUrl() {
        try {
            return sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        } catch (Exception ex) {
            sqsClient.createQueue(CreateQueueRequest.builder().queueName(queueName).build());
            return sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        }
    }
}
