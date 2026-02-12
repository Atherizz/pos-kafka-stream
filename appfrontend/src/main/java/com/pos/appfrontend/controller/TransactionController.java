package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.requests.TransactionRequest;
import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.service.TransactionProducerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Transaction Controller", description = "API for Transaction management")
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionProducerService producerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> sendTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Received transaction ItemCount={}",
                request.getItems().size());

        try {
            producerService.sendTransaction(request);

            return ResponseEntity.accepted().body(new ApiResponse<>(
                    "SUCCESS",
                    "Transaction has been accepted and is being processed",
                    null
            ));
        } catch (RuntimeException e) {
            log.error("Transaction failed validation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "ERROR",
                    e.getMessage(),
                    null
            ));
        } catch (Exception e) {
            log.error("Unexpected error during transaction production", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "SERVER_ERROR",
                    "An unexpected error occurred",
                    null
            ));
        }
    }
}
