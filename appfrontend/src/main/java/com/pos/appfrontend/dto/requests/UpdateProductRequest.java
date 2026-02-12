package com.pos.appfrontend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Pattern(regexp = "^SKU.*", message = "SKU must start with 'SKU'")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    @JsonProperty("product_name")
    private String productName;

    @NotNull(message = "Category ID is required")
    @JsonProperty("category_id")
    private Integer categoryId;

    @NotNull(message = "Supplier ID is required")
    @JsonProperty("supplier_id")
    private Integer supplierId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal price;
}

