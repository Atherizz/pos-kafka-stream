package com.pos.appfrontend.util;

import com.pos.appfrontend.dto.requests.ProductRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class ValidationUtil {

    @Autowired
    private Validator validator;

    public <T> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            log.warn("Validation failed for {}: {} violations found",
                    request.getClass().getSimpleName(),
                    violations.size());
            throw new ConstraintViolationException(violations);
        }
    }

    public <T> T validateIsFound(Optional<T> optional, String entityName, Object id) {
        if (!optional.isPresent()) {
            log.warn("{} with ID {} not found", entityName, id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + " not found");
        }
        return optional.get();
    }

    public void validatePasswordMatch(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            log.warn("Password and Confirm Password do not match");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passwords do not match!");
        }

    }

}
