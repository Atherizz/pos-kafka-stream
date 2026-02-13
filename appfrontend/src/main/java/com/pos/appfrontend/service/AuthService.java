package com.pos.appfrontend.service;

import com.pos.appfrontend.configuration.jwt.JwtService;
import com.pos.appfrontend.dto.requests.LoginRequest;
import com.pos.appfrontend.dto.requests.RegisterRequest;
import com.pos.appfrontend.dto.responses.AuthResponse;
import com.pos.appfrontend.model.User;
import com.pos.appfrontend.repository.UserRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.debug("Processing business logic for login user: {}", request.getEmail());
        validationUtil.validate(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: Email {} not found", request.getEmail());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Wrong password for email {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 hour in seconds
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Processing business logic for registering user: {}", request.getEmail());
        validationUtil.validate(request);
        validationUtil.validatePasswordMatch(request.getPassword(), request.getConfirmPassword());

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String hashed = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashed);

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Generate token for newly registered user
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 hour in seconds
                .build();
    }
}
