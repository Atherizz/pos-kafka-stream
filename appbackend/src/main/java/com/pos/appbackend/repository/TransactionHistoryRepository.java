package com.pos.appbackend.repository;

import com.pos.appbackend.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
    TransactionHistory findByTransactionUuid(String transactionUuid);
}
