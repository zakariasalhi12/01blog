package com._blog._blog.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com._blog._blog.dto.LoginRequest;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.utils.JwtUtil;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public LoginService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<Map<String, String>> login(LoginRequest loginRequest) {

        Map<String, String> response = new HashMap<>();

        try {
            if (loginRequest == null || loginRequest.getLogin() == null || loginRequest.getLogin().isBlank()) {
                response.put("error", "Missing login");
                return ResponseEntity.badRequest().body(response);
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
                response.put("error", "Missing password");
                return ResponseEntity.badRequest().body(response);
            }

            String login = loginRequest.getLogin();

            // Detect email or username
            boolean isEmail = login.contains("@");

            Optional<User> optionalUser;
            if (isEmail) {
                optionalUser = userRepository.findByEmail(login);
                if (!optionalUser.isPresent()) {
                    response.put("error", "Email not found");
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                optionalUser = userRepository.findByUsername(login);
                if (!optionalUser.isPresent()) {
                    response.put("error", "Username not found");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            User user = optionalUser.get();

            // Authenticate using username internally
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(), // always authenticate by username
                    loginRequest.getPassword()
                )
            );

            // Generate JWT
            String token = jwtUtil.generateToken(user);

            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}
