package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.requests.SupplierRequest;
import com.pos.appfrontend.model.Supplier;
import com.pos.appfrontend.repository.SupplierRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Transactional
    public void createSupplier(SupplierRequest request) {
        log.debug("Processing business logic for creating supplier: {}", request);
        validationUtil.validate(request);

        Supplier supplier = new Supplier();
        supplier.setSupplierName(request.getSupplierName());
        supplier.setContact(request.getContact());
        supplier.setAddress(request.getAddress());

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Successfully persisted Supplier to database with ID: {}", savedSupplier.getId());
    }

    @Transactional
    public List<Supplier> listSuppliers() {
        log.info("Fetching all suppliers from database");
        List<Supplier> suppliers = supplierRepository.findAll();
        log.debug("Retrieved {} suppliers", suppliers.size());
        return suppliers;
    }

    @Transactional
    public Supplier getSupplierById(Integer id) {
        log.info("Fetching supplier with ID: {}", id);
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        return validationUtil.validateIsFound(supplierOptional, "Supplier", id);
    }

    @Transactional
    public Supplier updateSupplier(Integer id, SupplierRequest request) {
        log.debug("Processing update for supplier ID: {} with data: {}", id, request);
        validationUtil.validate(request);

        Supplier supplier = getSupplierById(id);
        supplier.setSupplierName(request.getSupplierName());
        supplier.setContact(request.getContact());
        supplier.setAddress(request.getAddress());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Successfully updated supplier ID: {}", id);
        return updatedSupplier;
    }

    @Transactional
    public void deleteSupplier(Integer id) {
        log.info("Attempting to delete supplier with ID: {}", id);
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
        log.info("Successfully deleted supplier ID: {}", id);
    }
}

