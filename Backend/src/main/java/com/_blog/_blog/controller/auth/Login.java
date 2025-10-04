package com._blog._blog.controller.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.dto.LoginRequest;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.JwtBlacklist;
import com._blog._blog.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class Login {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private final JwtBlacklist jwtBlacklist;
    private final JwtUtil jwtUtil;

    public Login(JwtBlacklist jwtBlacklist, JwtUtil jwtUtil) {
        this.jwtBlacklist = jwtBlacklist;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            // Authenticate username/password
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Get user from DB
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }

}
