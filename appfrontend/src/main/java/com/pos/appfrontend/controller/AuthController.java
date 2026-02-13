package com.pos.appfrontend.controller;

import com.pos.appfrontend.dto.requests.LoginRequest;
import com.pos.appfrontend.dto.requests.RegisterRequest;
import com.pos.appfrontend.dto.responses.ApiResponse;
import com.pos.appfrontend.dto.responses.AuthResponse;
import com.pos.appfrontend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Authentication", description = "API for user authentication and registration")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Receiving request to register user with email: {}", request.getEmail());

        AuthResponse authResponse = authService.register(request);

        log.info("User registered successfully: {}", request.getEmail());

        return ApiResponse.<AuthResponse>builder()
                .message("User registered successfully")
                .data(authResponse)
                .build();
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Receiving login request for email: {}", request.getEmail());

        AuthResponse authResponse = authService.login(request);

        log.info("User logged in successfully: {}", request.getEmail());

        return ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .data(authResponse)
                .build();
    }
}

