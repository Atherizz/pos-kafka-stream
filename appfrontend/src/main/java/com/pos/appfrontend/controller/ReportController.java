package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.dto.responses.SalesReportResponse;
import com.pos.appfrontend.dto.responses.StockLogReportResponse;
import com.pos.appfrontend.service.StockLogService;
import com.pos.appfrontend.service.TransactionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(
            summary = "Get Sales Report",
            description = "Retrieves sales report with optional date range filtering. Returns all transaction details with product and pricing information."
    )
    @GetMapping(path = "/sales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<SalesReportResponse>> getSalesReport(
            @Parameter(description = "Start date for filtering (format: yyyy-MM-dd)", example = "2026-02-01")
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (format: yyyy-MM-dd)", example = "2026-02-28")
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

    @Operation(
            summary = "Get Stock Log Report",
            description = "Retrieves stock log report with optional date range and log type filtering. Log types: SALE (stock reduced), PURCHASE (stock increased), ADJUSTMENT (manual correction)."
    )
    @GetMapping(path = "/stock-logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<StockLogReportResponse>> getStockLogReport(
            @Parameter(description = "Start date for filtering (format: yyyy-MM-dd)", example = "2026-02-01")
            @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (format: yyyy-MM-dd)", example = "2026-02-28")
            @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Log type for filtering (SALE, PURCHASE, ADJUSTMENT)", schema = @Schema(allowableValues = {"SALE", "PURCHASE", "ADJUSTMENT"}), example = "SALE")
            @RequestParam(name = "log_type", required = false) String logType) {

        if (startDate != null && endDate != null && logType != null) {
            log.info("Receiving request for stock log report from {} to {} with log type: {}", startDate, endDate, logType);
        } else if (startDate != null && endDate != null) {
            log.info("Receiving request for stock log report from {} to {}", startDate, endDate);
        } else if (logType != null) {
            log.info("Receiving request for stock log report with log type: {}", logType);
        } else {
            log.info("Receiving request for stock log report (all data)");
        }

        List<StockLogReportResponse> stockLogReport = stockLogService.getStockLogReport(startDate, endDate, logType);

        log.info("Successfully retrieved stock log report with {} records", stockLogReport.size());

        return ApiResponse.<List<StockLogReportResponse>>builder()
                .message("Successfully retrieved stock log report")
                .data(stockLogReport)
                .build();
    }
}
