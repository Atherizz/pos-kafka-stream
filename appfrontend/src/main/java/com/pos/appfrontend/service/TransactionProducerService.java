package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.requests.TransactionEvent;
import com.pos.appfrontend.dto.requests.TransactionItemRequest;
import com.pos.appfrontend.dto.requests.TransactionRequest;
import com.pos.appfrontend.model.Product;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducerService {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Autowired
    private final ProductService productService;

    public void sendTransaction(TransactionRequest transactionRequest) {

        for (TransactionItemRequest item : transactionRequest.getItems()) {
            productService.validateStock(item.getProductId(), item.getQty());
        }

        String uuid = UUID.randomUUID().toString();

        List<Integer> productIds = transactionRequest.getItems().stream()
                .map(TransactionItemRequest::getProductId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, Product> productMap = productService.getProductsByIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        log.info("Processing transaction UUID: {} with {} items", uuid, transactionRequest.getItems().size());

        // Get current user email from JWT authentication
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        String currentUserEmail = (principal instanceof com.pos.appfrontend.model.User)
                ? ((com.pos.appfrontend.model.User) principal).getEmail()
                : (principal != null ? principal.toString() : "anonymous");

        for(TransactionItemRequest item: transactionRequest.getItems()) {
            Product product = productMap.get(item.getProductId());

            if (product != null) {
                TransactionEvent event = TransactionEvent.builder()
                        .transactionUuid(uuid)
                        .logType("SALE")
                        .userEmail(currentUserEmail)
                        .productId(product.getId())
                        .qty(item.getQty())
                        .price(product.getPrice())
                        .createdAt(LocalDateTime.now())
                        .build();

                log.debug("Sending to Kafka - Product: {}, Qty: {}, Price: {}, User: {}",
                        product.getProductName(), item.getQty(), product.getPrice(), currentUserEmail);

                kafkaTemplate.send("sales-transaction-savero",
                        String.valueOf(item.getProductId()),
                        event);
            }
        }

        log.info("Successfully sent {} transaction events to Kafka", transactionRequest.getItems().size());
    }
}
