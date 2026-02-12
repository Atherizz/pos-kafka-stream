package com.pos.appbackend.service;

import com.pos.appbackend.dto.TransactionEvent;
import com.pos.appbackend.model.Product;
import com.pos.appbackend.model.TransactionDetail;
import com.pos.appbackend.model.TransactionHistory;
import com.pos.appbackend.repository.ProductRepository;
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

    @KafkaListener(topics = "sales-transaction-savero", groupId = "report-group")
    @Transactional
    public void processTransaction(ConsumerRecord<Integer, TransactionEvent> record) {
        log.info("======= PROCESSING SALES EVENT =======");

        TransactionEvent event = record.value();
        log.info("UUID: {}, Product ID: {}, Qty: {}",
                event.getTransactionUuid(), event.getProductId(), event.getQty());

        // 1. Create history if not exist
        TransactionHistory history = transactionHistoryService.createOrGet(
                event.getTransactionUuid(),
                LocalDate.now()
        );
        log.info("Transaction History ID: {}", history.getId());

        // 2. Get product
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + event.getProductId()));
        log.info("Product found: {} (Stock: {})", product.getProductName(), product.getCurrentStock());

        // 3. Create transaction detail
        TransactionDetail detail = new TransactionDetail();
        detail.setTransaction(history);
        detail.setProduct(product);
        detail.setQty(event.getQty());
        detail.setPrice(product.getPrice());

        BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(event.getQty()));
        detail.setTotalPrice(itemTotal);

        transactionDetailRepository.save(detail);
        log.info("Transaction detail saved. Item total: {}", itemTotal);

        // 4. Update total amount in history
        BigDecimal newTotal = history.getTotalPrice().add(itemTotal);
        history.setTotalPrice(newTotal);
        transactionHistoryService.save(history);
        log.info("Updated transaction total: {}", newTotal);



    }
}
