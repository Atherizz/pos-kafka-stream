package com.pos.appfrontend.repository;

import com.pos.appfrontend.model.StockLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockLogRepository extends JpaRepository<StockLog, Integer> {

    @Query("SELECT sl FROM StockLog sl WHERE sl.createdAt BETWEEN :startDateTime AND :endDateTime ORDER BY sl.createdAt DESC")
    List<StockLog> findByCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT sl FROM StockLog sl WHERE sl.createdAt BETWEEN :startDateTime AND :endDateTime AND sl.logType = :logType ORDER BY sl.createdAt DESC")
    List<StockLog> findByCreatedAtBetweenAndLogType(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("logType") String logType);

    @Query("SELECT sl FROM StockLog sl WHERE sl.logType = :logType ORDER BY sl.createdAt DESC")
    List<StockLog> findByLogType(@Param("logType") String logType);

    @Query("SELECT sl FROM StockLog sl ORDER BY sl.createdAt DESC")
    List<StockLog> findAllOrderByCreatedAtDesc();
}
