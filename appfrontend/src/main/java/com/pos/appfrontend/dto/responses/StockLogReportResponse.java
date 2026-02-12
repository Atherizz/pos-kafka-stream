package com.pos.appfrontend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockLogReportResponse {

    private Integer no;

    @JsonProperty("log_id")
    private Integer logId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("qty_change")
    private Integer qtyChange;

    @JsonProperty("log_type")
    private String logType;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}

