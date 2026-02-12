package com.pos.appfrontend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesReportResponse {

    private Integer no;

    @JsonProperty("transaction_id")
    private Integer transactionId;

    @JsonProperty("transaction_date")
    private LocalDate transactionDate;

    @JsonProperty("product_name")
    private String productName;

    private Integer qty;

    private BigDecimal price;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;
}

