package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.requests.SupplierRequest;
import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.model.Supplier;
import com.pos.appfrontend.service.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Supplier Controller", description = "API for Suppliers management")
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> createSupplier(@RequestBody SupplierRequest request) {
        log.info("Receiving request to create supplier: {}", request.getSupplierName());
        supplierService.createSupplier(request);

        log.info("Supplier created successfully, returning response");
        return ApiResponse.<String>builder()
                .message("Supplier created successfully")
                .data(null)
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<Supplier>> listSuppliers() {
        log.info("Receiving request to list all suppliers");
        List<Supplier> suppliers = supplierService.listSuppliers();

        log.info("Successfully retrieved {} suppliers", suppliers.size());
        return ApiResponse.<List<Supplier>>builder()
                .message("Successfully retrieved all suppliers")
                .data(suppliers)
                .build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Supplier> getSupplierById(@PathVariable Integer id) {
        log.info("Receiving request to get supplier with ID: {}", id);
        Supplier supplier = supplierService.getSupplierById(id);

        log.info("Supplier with ID: {} found", id);
        return ApiResponse.<Supplier>builder()
                .message("Supplier found")
                .data(supplier)
                .build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Supplier> updateSupplier(@PathVariable Integer id, @Valid @RequestBody SupplierRequest request) {
        log.info("Receiving request to update supplier with ID: {}", id);
        Supplier supplier = supplierService.updateSupplier(id, request);

        log.info("Supplier with ID: {} updated successfully", id);
        return ApiResponse.<Supplier>builder()
                .message("Supplier successfully updated")
                .data(supplier)
                .build();
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> deleteSupplier(@PathVariable Integer id) {
        log.info("Receiving request to delete supplier with ID: {}", id);
        supplierService.deleteSupplier(id);

        log.info("Supplier with ID: {} deleted successfully", id);
        return ApiResponse.<String>builder()
                .message("Supplier successfully deleted")
                .data("Deleted Supplier ID: " + id)
                .build();
    }
}

