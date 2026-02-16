package com.pos.appbackend.service;

import com.pos.appbackend.dto.TransactionStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerService {

    private final KafkaTemplate<String, TransactionStatusEvent> kafkaTemplate;

    public void sendNotification(TransactionStatusEvent status, String transactionId) {
        kafkaTemplate.send("transaction-status-topic", transactionId, status);
        log.info("Sent notification for transaction ID {}: {}", transactionId, status);
    }
}
