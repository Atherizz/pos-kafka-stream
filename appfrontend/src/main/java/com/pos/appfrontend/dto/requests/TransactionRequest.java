package com.pos.appfrontend.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(message = "Transaction date is required")
    @JsonProperty("transaction_date")
    private LocalDate transactionDate;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<TransactionItemRequest> items;
}
