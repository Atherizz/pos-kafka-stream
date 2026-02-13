package com.pos.appfrontend.service;

import com.pos.appfrontend.dto.requests.RegisterRequest;
import com.pos.appfrontend.model.User;
import com.pos.appfrontend.repository.UserRepository;
import com.pos.appfrontend.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void register(RegisterRequest request) {
        log.debug("Processing business logic for registering user: {}", request);
        validationUtil.validate(request);
        validationUtil.validatePasswordMatch(request.getPassword(), request.getConfirmPassword());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String hashed = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashed);

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
    }

    @Transactional
    public List<User> getAllUsers() {
        log.info("Fetching all users from database");
        List<User> users = userRepository.findAll();
        log.debug("Retrieved {} users", users.size());
        return users;
    }

}