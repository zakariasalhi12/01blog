package com._blog._blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.dto.ProfileResponse;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

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

    public ResponseEntity<?> updateUser(Long id, User updateData, MultipartFile avatarFile) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUserOpt = userRepository.findByUsername(username);

        if (loggedUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User loggedUser = loggedUserOpt.get();

        // 2️⃣ Check authorization
        boolean isAdmin = loggedUser.getRole().equals("ADMIN"); // assuming User has getRole()
        boolean isSelf = loggedUser.getId().equals(id);

        if (!isSelf && !isAdmin) {
            return ResponseEntity.status(403).body("Forbidden: Cannot update another user");
        }

        // 3️⃣ Load target user
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOpt.get();

        // 4️⃣ Update fields as before
        if (updateData.getUsername() != null) {
            user.setUsername(updateData.getUsername());
        }
        if (updateData.getEmail() != null) {
            user.setEmail(updateData.getEmail());
        }
        if (updateData.getPassword() != null) {
            user.setPassword(updateData.getPassword());
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            ResponseEntity<?> fileResponse = fileStorageService.storeFile(avatarFile);
            if (fileResponse.getStatusCode().is2xxSuccessful()) {
                @SuppressWarnings("unchecked")
                var body = (java.util.Map<String, String>) fileResponse.getBody();
                user.setAvatar("/uploads/" + body.get("fileName"));
            } else {
                return fileResponse;
            }
        }

        userRepository.save(user);

        return ResponseEntity.ok("User updated successfully");
    }
}
