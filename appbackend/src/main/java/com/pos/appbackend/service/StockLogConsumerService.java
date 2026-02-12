package com.pos.appbackend.service;

import com.pos.appbackend.dto.TransactionEvent;
import com.pos.appbackend.model.Product;
import com.pos.appbackend.model.StockLog;
import com.pos.appbackend.repository.ProductRepository;
import com.pos.appbackend.repository.StockLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockLogConsumerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockLogRepository stockLogRepository;

    @KafkaListener(topics = "sales-transaction-savero", groupId = "stock-log-group")
    @Transactional
    public void consume(ConsumerRecord<Integer, TransactionEvent> record) {

        TransactionEvent event = record.value();

        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + event.getProductId()));
        log.info("Product found: {} (Stock: {})", product.getProductName(), product.getCurrentStock());

        Integer currentStock = product.getCurrentStock();
        Integer newStock = currentStock - event.getQty();

        if (newStock < 0) {
            log.error("ABORT! Insufficient stock for Product: {}. Available: {}, Requested: {}",
                    product.getProductName(), currentStock, event.getQty());
            throw new RuntimeException("Insufficient stock for product " + product.getId());
        }

        product.setCurrentStock(newStock);
        productRepository.save(product);
        log.info("Product stock updated: {} -> {}", currentStock, newStock);

            StockLog newStockLog = new StockLog();
            newStockLog.setProduct(product);
            newStockLog.setLogType(event.getLogType());
            newStockLog.setQuantityChange(-event.getQty());
            newStockLog.setCreatedAt(event.getCreatedAt());

            stockLogRepository.save(newStockLog);
            log.info("StockLog created for Product ID: {} with Qty Change: {}", product.getId(), event.getQty());

    }
}
