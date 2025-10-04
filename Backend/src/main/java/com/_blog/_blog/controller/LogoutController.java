package com._blog._blog.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.JwtBlacklist;
import com._blog._blog.utils.JwtUtil;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private final JwtBlacklist jwtBlacklist;
    private final JwtUtil jwtUtil;

    public LogoutController(JwtBlacklist jwtBlacklist, JwtUtil jwtUtil) {
        this.jwtBlacklist = jwtBlacklist;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            LocalDateTime expiresAt = jwtUtil.extractExpiration(token);
            jwtBlacklist.addToken(token, expiresAt);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
