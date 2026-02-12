package com.pos.appfrontend.repository;

import com.pos.appfrontend.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {

    @Query("SELECT th FROM TransactionHistory th WHERE th.transactionDate BETWEEN :startDate AND :endDate ORDER BY th.transactionDate DESC")
    List<TransactionHistory> findByTransactionDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

