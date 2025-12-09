package com._blog._blog.service;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com._blog._blog.utils.JwtUtil;

@Service
public class LogoutService {

    @Autowired
    private JwtBlacklist jwtBlacklist;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            LocalDateTime expiresAt = jwtUtil.extractExpiration(token);
            jwtBlacklist.addToken(token, expiresAt);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

}