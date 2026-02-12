package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.requests.CategoryRequest;
import com.pos.appfrontend.model.Category;
import com.pos.appfrontend.repository.CategoryRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Transactional
    public void createCategory(CategoryRequest request) {
        log.debug("Processing business logic for creating category: {}", request);
        validationUtil.validate(request);

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());

        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully persisted Category to database with ID: {}", savedCategory.getId());
    }

    @Transactional
    public List<Category> listCategories() {
        log.info("Fetching all categories from database");
        List<Category> categories = categoryRepository.findAll();
        log.debug("Retrieved {} categories", categories.size());
        return categories;
    }

    @Transactional
    public Category getCategoryById(Integer id) {
        log.info("Fetching category with ID: {}", id);
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return validationUtil.validateIsFound(categoryOptional, "Category", id);
    }

    @Transactional
    public Category updateCategory(Integer id, CategoryRequest request) {
        log.debug("Processing update for category ID: {} with data: {}", id, request);
        validationUtil.validate(request);

        Category category = getCategoryById(id);
        category.setCategoryName(request.getCategoryName());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Successfully updated category ID: {}", id);
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Integer id) {
        log.info("Attempting to delete category with ID: {}", id);
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        log.info("Successfully deleted category ID: {}", id);
    }
}

