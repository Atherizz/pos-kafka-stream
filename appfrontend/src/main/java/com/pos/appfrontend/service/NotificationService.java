package com.pos.appfrontend.service;

import com.pos.appfrontend.configuration.websocket.TransactionStatusHandler;
import com.pos.appfrontend.dto.responses.TransactionStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @KafkaListener(topics = "transaction-status-topic", groupId = "frontend-notification-group")
    public void listen(TransactionStatusEvent event) {
        log.info("Received notification message: {}", event.getMessage());
        TransactionStatusHandler.sendNotification(event);
    }
}
