package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.responses.SalesReportResponse;
import com.pos.appfrontend.model.TransactionDetail;
import com.pos.appfrontend.repository.TransactionDetailRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionDetailService {
    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Transactional
    public List<TransactionDetail> getAllTransactionDetails() {
        log.info("Fetching all transaction details from database");
        return transactionDetailRepository.findAll();
    }

    @Transactional
    public List<SalesReportResponse> getSalesReport(LocalDate startDate, LocalDate endDate) {
        List<TransactionDetail> details;

        if (startDate != null && endDate != null) {
            log.info("Fetching sales report from {} to {}", startDate, endDate);
            details = transactionDetailRepository.findByTransactionDateBetween(startDate, endDate);
        } else {
            log.info("Fetching all sales report (no date filter)");
            details = transactionDetailRepository.findAll();
        }

        log.info("Found {} transaction details", details.size());

        AtomicInteger counter = new AtomicInteger(1);

        return details.stream()
                .map(detail -> SalesReportResponse.builder()
                        .no(counter.getAndIncrement())
                        .transactionId(detail.getTransaction().getId())
                        .transactionDate(detail.getTransaction().getTransactionDate())
                        .productName(detail.getProduct().getProductName())
                        .qty(detail.getQty())
                        .price(detail.getPrice())
                        .totalPrice(detail.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
