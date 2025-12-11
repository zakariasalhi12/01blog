package com._blog._blog.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com._blog._blog.dto.ProfileResponse;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String username, String email, String password, String avatar, int age) {
        User user = new User(username, email, password, age);
        return userRepository.save(user);
    }


    public ResponseEntity<?> profile(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            ProfileResponse profileResponse = new ProfileResponse(user.getUsername(), user.getEmail(), user.getAvatar());
            return ResponseEntity.ok(profileResponse);
        } 
        return ResponseEntity.status(404).body("User not found");
    }
}