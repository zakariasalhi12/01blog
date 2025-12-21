package com._blog._blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.dto.FullProfileResponse;
import com._blog._blog.dto.ProfileResponse;
import com._blog._blog.dto.UserListResponse;
import com._blog._blog.models.User;
import com._blog._blog.repository.SubscriptionsRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    public User createUser(String username, String email, String password, String avatar, int age) {
        User user = new User(username, email, password, age);
        return userRepository.save(user);
    }

    public ResponseEntity<?> profile(Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUserOpt = userRepository.findByUsername(username);

        if (loggedUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User currentUser = loggedUserOpt.get();



        if (Objects.equals(currentUser.getId(), id)) {

            long followingCount = subscriptionsRepository.findAllBySubscriberId_Id(currentUser.getId()).size();
            long followersCount = subscriptionsRepository.findAllBySubscribedToId_Id(currentUser.getId()).size();

            FullProfileResponse profile = new FullProfileResponse(
                currentUser.getUsername(),
                currentUser.getAvatar(),
                currentUser.getCreatedAt().toString(),
                currentUser.getEmail(),
                currentUser.getAge(),
                currentUser.getRole().toString(),
                followersCount,
                followingCount
            );
            return ResponseEntity.ok(profile);
        }

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {

            long followingCount = subscriptionsRepository.findAllBySubscriberId_Id(currentUser.getId()).size();
            long followersCount = subscriptionsRepository.findAllBySubscribedToId_Id(currentUser.getId()).size();

            ProfileResponse profileResponse = new ProfileResponse(
                user.getUsername(),
                user.getAvatar(),
                user.getCreatedAt().toString(),
                followersCount,
                followingCount
            );
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

    public ResponseEntity<?> getAllUsers(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUserOpt = userRepository.findByUsername(username);

        if (loggedUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User loggedUser = loggedUserOpt.get();
        if (!loggedUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> usersPage = userRepository.findAll(pageable);

        List<UserListResponse> userList = usersPage.getContent().stream()
            .map(user -> new UserListResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString()
            ))
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userList);
        response.put("currentPage", usersPage.getNumber());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("totalUsers", usersPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateUserRole(Long userId, String role) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUserOpt = userRepository.findByUsername(username);

        if (loggedUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User loggedUser = loggedUserOpt.get();
        if (!loggedUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        
        // Prevent admin from changing their own role
        if (user.getId().equals(loggedUser.getId())) {
            return ResponseEntity.status(400).body("Cannot change your own role");
        }

        try {
            User.Role newRole = User.Role.valueOf(role.toUpperCase());
            user.setRole(newRole);
            userRepository.save(user);
            return ResponseEntity.ok("User role updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Invalid role: " + role);
        }
    }
}
