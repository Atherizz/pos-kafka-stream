package com.pos.appfrontend.repository;

import com.pos.appfrontend.model.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Integer> {

    @Query("SELECT td FROM TransactionDetail td JOIN td.transaction th WHERE th.transactionDate BETWEEN :startDate AND :endDate ORDER BY th.transactionDate DESC, td.id DESC")
    List<TransactionDetail> findByTransactionDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
