package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.requests.ProductRequest;
import com.pos.appfrontend.dto.requests.UpdateProductRequest;
import com.pos.appfrontend.dto.requests.UpdateStockRequest;
import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.dto.responses.ProductResponse;
import com.pos.appfrontend.model.Product;
import com.pos.appfrontend.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Product Controller", description = "API for Products management")
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Receiving request to create product: {}", productRequest.getProductName());
        productService.createProduct(productRequest);

        log.info("Product created successfully, returning response");
        return ApiResponse.<String>builder()
                .message("Product created successfully")
                .data(null)
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<ProductResponse>> listProducts() {
        log.info("Receiving request to list all products");
        List<ProductResponse> products = productService.listProducts();

        log.info("Successfully retrieved {} products", products.size());
        return ApiResponse.<List<ProductResponse>>builder()
                .message("Successfully retrieved all products")
                .data(products)
                .build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Product> getProductById(@PathVariable Integer id) {
        log.info("Receiving request to get product with ID: {}", id);
        Product product = productService.getProductById(id);

        log.info("Product with ID: {} found", id);
        return ApiResponse.<Product>builder()
                .message("Product found")
                .data(product)
                .build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Integer id, @Valid @RequestBody UpdateProductRequest productRequest) {
        log.info("Receiving request to update product with ID: {}", id);
        ProductResponse product = productService.updateProduct(id, productRequest);

        log.info("Product with ID: {} updated successfully", id);
        return ApiResponse.<ProductResponse>builder()
                .message("Product successfully updated")
                .data(product)
                .build();
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> deleteProduct(@PathVariable Integer id) {
        log.info("Receiving request to delete product with ID: {}", id);
        productService.deleteProduct(id);

        log.info("Product with ID: {} deleted successfully", id);
        return ApiResponse.<String>builder()
                .message("Product successfully deleted")
                .data("Deleted Product ID: " + id)
                .build();
    }

    @PatchMapping(path = "/{id}/stock", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> updateStock(@PathVariable Integer id, @Valid @RequestBody UpdateStockRequest request) {
        log.info("Receiving request to update stock for product ID: {} - Action: {}, Type: {}",
                id, request.getAction(), request.getType());
        ProductResponse product = productService.updateStock(id, request);

        log.info("Stock updated successfully for product ID: {}", id);
        return ApiResponse.<ProductResponse>builder()
                .message("Stock successfully updated")
                .data(product)
                .build();
    }
}