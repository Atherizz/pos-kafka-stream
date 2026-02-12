package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.requests.ProductRequest;
import com.pos.appfrontend.dto.requests.UpdateProductRequest;
import com.pos.appfrontend.dto.requests.UpdateStockRequest;
import com.pos.appfrontend.dto.responses.ProductResponse;
import com.pos.appfrontend.exception.BusinessException;
import com.pos.appfrontend.model.Category;
import com.pos.appfrontend.model.Product;
import com.pos.appfrontend.model.StockLog;
import com.pos.appfrontend.model.Supplier;
import com.pos.appfrontend.repository.CategoryRepository;
import com.pos.appfrontend.repository.ProductRepository;
import com.pos.appfrontend.repository.StockLogRepository;
import com.pos.appfrontend.repository.SupplierRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private StockLogRepository stockLogRepository;

    @Transactional
    public void createProduct(ProductRequest productRequest) {
        log.debug("Processing business logic for creating product: {}", productRequest);
        validationUtil.validate(productRequest);


        Product product = new Product();
        product.setSku(productRequest.getSku());
        product.setProductName(productRequest.getProductName());
        product.setCurrentStock(productRequest.getCurrentStock());
        product.setPrice(productRequest.getPrice());

        Category category = validationUtil.validateIsFound(
                categoryRepository.findById(productRequest.getCategoryId()),
                "Category",
                productRequest.getCategoryId());

        Supplier supplier = validationUtil.validateIsFound(
                supplierRepository.findById(productRequest.getSupplierId()),
                "Supplier",
                productRequest.getSupplierId());

        product.setCategory(category);
        product.setSupplier(supplier);

        Product savedProduct = productRepository.save(product);
        log.info("Successfully persisted Product to database with ID: {}", savedProduct.getId());
    }

    @Transactional
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .sku(product.getSku())
                        .productName(product.getProductName())
                        .categoryName(product.getCategory().getCategoryName())
                        .supplierName(product.getSupplier().getSupplierName())
                        .currentStock(product.getCurrentStock())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Product getProductById(Integer id) {
        log.info("Fetching product with ID: {}", id);
        Optional<Product> productOptional = productRepository.findById(id);

        return validationUtil.validateIsFound(productOptional, "Product", id);
    }

    public void validateStock(Integer productId, Integer qty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));

            if (product.getCurrentStock() < qty) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName() +
                        ". Available: " + product.getCurrentStock() +
                        ", Requested: " + qty);
        }

        log.debug("Stock validation passed for product: {} (Current Stock: {})",
                product.getProductName(), product.getCurrentStock());
    }

    @Transactional
    public List<Product> getProductsByIds(List<Integer> ids) {
        log.debug("Fetching {} products in batch", ids.size());
        return productRepository.findAllById(ids);
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request) {
        log.debug("Processing update for product ID: {} with data: {}", id, request);
        validationUtil.validate(request);

        Product product = getProductById(id);

        product.setSku(request.getSku());
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());

        Category category = validationUtil.validateIsFound(
                categoryRepository.findById(request.getCategoryId()),
                "Category",
                request.getCategoryId());

        Supplier supplier = validationUtil.validateIsFound(
                supplierRepository.findById(request.getSupplierId()),
                "Supplier",
                request.getSupplierId());

        product.setCategory(category);
        product.setSupplier(supplier);

        Product updatedProduct = productRepository.save(product);
        log.info("Successfully updated product ID: {} (stock unchanged)", id);

        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .sku(updatedProduct.getSku())
                .productName(updatedProduct.getProductName())
                .categoryName(category.getCategoryName())
                .supplierName(supplier.getSupplierName())
                .currentStock(updatedProduct.getCurrentStock())
                .price(updatedProduct.getPrice())
                .build();
    }

    @Transactional
    public void deleteProduct(Integer id) {
        log.info("Attempting to delete product with ID: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
        log.info("Successfully deleted product ID: {}", id);
    }

    @Transactional
    public ProductResponse updateStock(Integer id, UpdateStockRequest request) {
        log.info("Processing stock update for product ID: {} with action: {}, type: {}",
                id, request.getAction(), request.getType());
        validationUtil.validate(request);

        Product product = getProductById(id);

        // Business rule: PURCHASE must use ADD action (cannot SET absolute value)
        if ("PURCHASE".equals(request.getType()) && "SET".equals(request.getAction())) {
            throw new BusinessException("Invalid operation: PURCHASE type must use ADD action, not SET");
        }

        int oldStock = product.getCurrentStock();
        int newStock;
        int quantityChange;


        if ("ADD".equals(request.getAction())) {
            newStock = oldStock + request.getQty();
            quantityChange = request.getQty();
            log.debug("ADD action: {} + {} = {}", oldStock, request.getQty(), newStock);
        } else if ("SET".equals(request.getAction())) {
            newStock = request.getQty();
            quantityChange = newStock - oldStock;
            log.debug("SET action: {} -> {} (change: {})", oldStock, newStock, quantityChange);
        } else {
            throw new IllegalArgumentException("Invalid action: " + request.getAction());
        }

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative. Current: " + oldStock +
                    ", Requested change: " + quantityChange);
        }

        product.setCurrentStock(newStock);
        Product updatedProduct = productRepository.save(product);

        StockLog stockLog = new StockLog();
        stockLog.setProduct(product);
        stockLog.setQuantityChange(quantityChange);
        stockLog.setLogType(request.getType());
        stockLogRepository.save(stockLog);

        log.info("Stock updated for product ID: {} from {} to {}. Type: {}",
                id, oldStock, newStock, request.getType());

        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .sku(updatedProduct.getSku())
                .productName(updatedProduct.getProductName())
                .categoryName(updatedProduct.getCategory().getCategoryName())
                .supplierName(updatedProduct.getSupplier().getSupplierName())
                .currentStock(updatedProduct.getCurrentStock())
                .price(updatedProduct.getPrice())
                .build();
    }
}