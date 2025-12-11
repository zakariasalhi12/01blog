package com._blog._blog.controller;

import java.security.Security;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.SubscribeService;
import com._blog._blog.service.UserService;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/profile/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("id") Long subscribedToId) {
        return subscribeService.subscribe(subscribedToId);
    }

    @GetMapping("/profile/me")
    public ResponseEntity<?> getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        return userService.profile(user.get().getId()); // Replace 1L with the actual logged-in user's ID
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        return userService.profile(id);
    }


    // @PutMapping("/profile")
    
    
}
