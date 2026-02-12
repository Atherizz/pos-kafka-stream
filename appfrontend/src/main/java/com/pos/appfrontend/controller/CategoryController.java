package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.requests.CategoryRequest;
import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.model.Category;
import com.pos.appfrontend.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "Category Controller", description = "API for Categories management")
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> createCategory(@RequestBody CategoryRequest request) {
        log.info("Receiving request to create category: {}", request.getCategoryName());
        categoryService.createCategory(request);

        log.info("Category created successfully, returning response");
        return ApiResponse.<String>builder()
                .message("Category created successfully")
                .data(null)
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<Category>> listCategories() {
        log.info("Receiving request to list all categories");
        List<Category> categories = categoryService.listCategories();

        log.info("Successfully retrieved {} categories", categories.size());
        return ApiResponse.<List<Category>>builder()
                .message("Successfully retrieved all categories")
                .data(categories)
                .build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Category> getCategoryById(@PathVariable Integer id) {
        log.info("Receiving request to get category with ID: {}", id);
        Category category = categoryService.getCategoryById(id);

        log.info("Category with ID: {} found", id);
        return ApiResponse.<Category>builder()
                .message("Category found")
                .data(category)
                .build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Category> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryRequest request) {
        log.info("Receiving request to update category with ID: {}", id);
        Category category = categoryService.updateCategory(id, request);

        log.info("Category with ID: {} updated successfully", id);
        return ApiResponse.<Category>builder()
                .message("Category successfully updated")
                .data(category)
                .build();
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> deleteCategory(@PathVariable Integer id) {
        log.info("Receiving request to delete category with ID: {}", id);
        categoryService.deleteCategory(id);

        log.info("Category with ID: {} deleted successfully", id);
        return ApiResponse.<String>builder()
                .message("Category successfully deleted")
                .data("Deleted Category ID: " + id)
                .build();
    }
}

