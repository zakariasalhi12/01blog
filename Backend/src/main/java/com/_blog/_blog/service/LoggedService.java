package com._blog._blog.service;

import java.util.HashMap;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoggedService {

    public ResponseEntity<?> checkLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Not logged in (no auth or no principal)
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        String username = auth.getName();

        // Extract role
        String role = auth.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("NO_ROLE");

        // Return JSON object with user info
        return ResponseEntity.ok(
                new HashMap<String, Object>() {
            {
                put("logged", true);
                put("username", username);
                put("role", role);
            }
        }
        );
    }
}
