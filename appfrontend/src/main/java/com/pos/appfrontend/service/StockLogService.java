package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.responses.StockLogReportResponse;
import com.pos.appfrontend.model.StockLog;
import com.pos.appfrontend.repository.StockLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockLogService {

    @Autowired
    private StockLogRepository stockLogRepository;

    @Transactional
    public List<StockLogReportResponse> getStockLogReport(LocalDate startDate, LocalDate endDate) {
        List<StockLog> stockLogs;

        if (startDate != null && endDate != null) {
            log.info("Fetching stock log report from {} to {}", startDate, endDate);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            stockLogs = stockLogRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        } else {
            log.info("Fetching all stock log report (no date filter)");
            stockLogs = stockLogRepository.findAll();
        }

        log.info("Found {} stock logs", stockLogs.size());

        AtomicInteger counter = new AtomicInteger(1);

        return stockLogs.stream()
                .map(stockLog -> StockLogReportResponse.builder()
                        .no(counter.getAndIncrement())
                        .logId(stockLog.getId())
                        .productName(stockLog.getProduct().getProductName())
                        .qtyChange(stockLog.getQuantityChange())
                        .logType(stockLog.getLogType())
                        .createdAt(stockLog.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
