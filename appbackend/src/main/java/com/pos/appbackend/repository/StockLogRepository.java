package com.pos.appbackend.repository;

import com.pos.appbackend.model.StockLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockLogRepository extends JpaRepository<StockLog, Integer> {

}
