package com.pos.appfrontend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer id;
    private String sku;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("current_stock")
    private Integer currentStock;

    private BigDecimal price;
}

