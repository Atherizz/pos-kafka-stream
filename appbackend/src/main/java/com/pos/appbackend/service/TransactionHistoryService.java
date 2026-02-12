package com.pos.appbackend.service;

import com.pos.appbackend.model.TransactionHistory;
import com.pos.appbackend.repository.TransactionHistoryRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@Data
public class TransactionHistoryService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    public TransactionHistory createOrGet(String uuid, LocalDate transactionDate) {
        log.info("Fetching or creating transaction history with UUID: {}", uuid);
        TransactionHistory history = transactionHistoryRepository.findByTransactionUuid(uuid);
        if (history == null) {
            log.info("Creating new transaction history for UUID: {}", uuid);
            history = new TransactionHistory();
            history.setTransactionUuid(uuid);
            history.setTransactionDate(transactionDate);
            history.setTotalPrice(BigDecimal.ZERO);
            history = transactionHistoryRepository.save(history);
        }
        return history;
    }

    public TransactionHistory save(TransactionHistory history) {
        log.info("Saving transaction history: {}", history.getTransactionUuid());
        return transactionHistoryRepository.save(history);
    }
}
