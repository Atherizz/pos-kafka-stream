package com.pos.appfrontend.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateStockRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer qty;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(ADD|SET)$", message = "Action must be either ADD or SET")
    @Schema(allowableValues = {"ADD", "SET"}, description = "Action to perform on stock")
    private String action;

    @NotBlank(message = "Type is required")
    @Schema(allowableValues = {"PURCHASE", "ADJUSTMENT"}, description = "Reason for stock update")
    @Pattern(regexp = "^(PURCHASE|ADJUSTMENT)$", message = "Type must be either PURCHASE or ADJUSTMENT")
    private String type;

}


