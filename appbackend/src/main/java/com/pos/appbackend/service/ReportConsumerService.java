package com.pos.appbackend.service;

import com.pos.appbackend.dto.TransactionEvent;
import com.pos.appbackend.dto.TransactionStatusEvent;
import com.pos.appbackend.model.Product;
import com.pos.appbackend.model.StockLog;
import com.pos.appbackend.model.TransactionDetail;
import com.pos.appbackend.model.TransactionHistory;
import com.pos.appbackend.repository.ProductRepository;
import com.pos.appbackend.repository.StockLogRepository;
import com.pos.appbackend.repository.TransactionDetailRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class ReportConsumerService {

    @Autowired
    private TransactionHistoryService transactionHistoryService;
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockLogRepository stockLogRepository;
    @Autowired
    private NotificationProducerService notificationProducerService;

    @KafkaListener(topics = "sales-transaction-savero", groupId = "report-group")
    @Transactional
    public void processTransaction(ConsumerRecord<Integer, TransactionEvent> record) {
        TransactionEvent event = record.value();
        String uuid = event.getTransactionUuid();

        log.info("======= PROCESSING TRANSACTION {} =======", uuid);

        try {
            Product product = productRepository.findById(event.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + event.getProductId()));

            log.info("Product found: {} (Stock: {})", product.getProductName(), product.getCurrentStock());

            Integer currentStock = product.getCurrentStock();
            Integer newStock = currentStock - event.getQty();

            if (newStock < 0) {
                String errorMsg = String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                        product.getProductName(), currentStock, event.getQty());
                log.error("ABORT! {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

            product.setCurrentStock(newStock);
            productRepository.save(product);
            log.info("Product stock updated: {} -> {}", currentStock, newStock);

            // STEP 2: Create Stock Log
            StockLog stockLog = new StockLog();
            stockLog.setProduct(product);
            stockLog.setLogType(event.getLogType());
            stockLog.setQuantityChange(-event.getQty());
            stockLog.setCreatedAt(event.getCreatedAt());
            stockLogRepository.save(stockLog);
            log.info("StockLog created for Product ID: {} with Qty Change: {}", product.getId(), -event.getQty());

            // STEP 3: Create Transaction History
            TransactionHistory history = transactionHistoryService.createOrGet(uuid, LocalDate.now());
            log.info("Transaction History ID: {}", history.getId());

            // STEP 4: Create Transaction Detail
            TransactionDetail detail = new TransactionDetail();
            detail.setTransaction(history);
            detail.setProduct(product);
            detail.setQty(event.getQty());
            detail.setPrice(product.getPrice());

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(event.getQty()));
            detail.setTotalPrice(itemTotal);
            transactionDetailRepository.save(detail);
            log.info("Transaction detail saved. Item total: {}", itemTotal);

            // STEP 5: Update Total Amount in History
            BigDecimal newTotal = history.getTotalPrice().add(itemTotal);
            history.setTotalPrice(newTotal);
            transactionHistoryService.save(history);
            log.info("Updated transaction total: {}", newTotal);

            // STEP 6: Send Success Notification
            String successMsg = String.format("Transaction processed successfully. Product: %s, Qty: %d, Total: %s",
                    product.getProductName(), event.getQty(), itemTotal);
            sendSuccessNotification(uuid, successMsg, event.getUserEmail());

            log.info("======= TRANSACTION {} COMPLETED SUCCESSFULLY =======", uuid);

        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", uuid, e.getMessage(), e);
            sendFailureNotification(uuid, e.getMessage(), event.getUserEmail());
            throw e; // Re-throw to trigger rollback
        }
    }

    private void sendSuccessNotification(String transactionUuid, String message, String userEmail) {
        TransactionStatusEvent statusEvent = TransactionStatusEvent.builder()
                .transactionUuid(transactionUuid)
                .email(userEmail)
                .status("SUCCESS")
                .message(message)
                .build();

        notificationProducerService.sendNotification(statusEvent, transactionUuid);
        log.info("Success notification sent for transaction: {}", transactionUuid);
    }

    private void sendFailureNotification(String transactionUuid, String errorMessage, String userEmail) {
        TransactionStatusEvent statusEvent = TransactionStatusEvent.builder()
                .transactionUuid(transactionUuid)
                .email(userEmail)
                .status("FAILED")
                .message(errorMessage)
                .build();

        notificationProducerService.sendNotification(statusEvent, transactionUuid);
        log.info("Failure notification sent for transaction: {}", transactionUuid);
    }
}
