package com.pos.appfrontend.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionStatusEvent implements Serializable {

    private String transactionUuid;
    private String status;
    private String message;
    private String email;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}