package com.pos.appbackend.service;

import com.pos.appbackend.dto.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MonitoringConsumerService {
    @KafkaListener(topics = "sales-transaction-savero", groupId = "logging-group")
    public void consume(TransactionEvent event) {
        if (event == null) {
            log.warn("monitoring_consumer received null event");
            return;
        }

        log.info(
                "monitoring_consumer event: uuid={} type={} productId={} qty={} price={} createdAt={}",
                event.getTransactionUuid(),
                event.getLogType(),
                event.getProductId(),
                event.getQty(),
                event.getPrice(),
                event.getCreatedAt()
        );
    }
}
