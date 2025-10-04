package com._blog._blog.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.dto.UserRequest;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.JwtBlacklist;
import com._blog._blog.utils.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class Signup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // JWT helper

    @Autowired
    private JwtBlacklist jwtBlacklist; // optional, for logout

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody UserRequest userRequest) {
        Map<String, String> response = new HashMap<>();

        // Check if username or email exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            response.put("error", "Username already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            response.put("error", "Email already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // Save new user
        User user = new User(
                userRequest.getUsername(),
                userRequest.getEmail(),
                passwordEncoder.encode(userRequest.getPassword()),
                userRequest.getAge()
        );

        User savedUser = userRepository.save(user);

        // Generate JWT
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name());

        response.put("token", token); // Return JWT
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Handle validation errors globally for this controller
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
