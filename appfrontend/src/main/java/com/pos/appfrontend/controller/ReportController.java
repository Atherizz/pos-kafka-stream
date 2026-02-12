package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.dto.responses.SalesReportResponse;
import com.pos.appfrontend.dto.responses.StockLogReportResponse;
import com.pos.appfrontend.service.StockLogService;
import com.pos.appfrontend.service.TransactionDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "Report Controller", description = "API for Report management")
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private TransactionDetailService transactionDetailService;

    @Autowired
    private StockLogService stockLogService;

    @GetMapping(path = "/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<SalesReportResponse>> getSalesReport(
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            log.info("Receiving request for sales report from {} to {}", startDate, endDate);
        } else {
            log.info("Receiving request for sales report (all data)");
        }

        List<SalesReportResponse> salesReport = transactionDetailService.getSalesReport(startDate, endDate);

        log.info("Successfully retrieved sales report with {} records", salesReport.size());

        return ApiResponse.<List<SalesReportResponse>>builder()
                .message("Successfully retrieved sales report")
                .data(salesReport)
                .build();
    }

    @GetMapping(path = "/stock-logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<StockLogReportResponse>> getStockLogReport(
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            log.info("Receiving request for stock log report from {} to {}", startDate, endDate);
        } else {
            log.info("Receiving request for stock log report (all data)");
        }

        List<StockLogReportResponse> stockLogReport = stockLogService.getStockLogReport(startDate, endDate);

        log.info("Successfully retrieved stock log report with {} records", stockLogReport.size());

        return ApiResponse.<List<StockLogReportResponse>>builder()
                .message("Successfully retrieved stock log report")
                .data(stockLogReport)
                .build();
    }
}
