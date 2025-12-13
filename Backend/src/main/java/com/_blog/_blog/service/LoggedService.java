package com._blog._blog.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;

@Service
public class LoggedService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> checkLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        // Not logged in (no auth or no principal)
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        String username = auth.getName();
        Optional<User> optionalUser  = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }


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
                put("username", username);
                put("role", role);
                put("avatar", userRepository.findByUsername(username).get().getAvatar());
            }
        }
        );
    }
}
