package com._blog._blog.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.dto.ReportRequest;
import com._blog._blog.models.User;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.SubscribeService;
import com._blog._blog.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com._blog._blog.service.ReportService;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private  ReportService reportService;

    @GetMapping("/profile/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam("id") Long subscribedToId) {
        return subscribeService.subscribe(subscribedToId);
    }

    @GetMapping("/profile/subscribe/check")
    public ResponseEntity<?> check(@RequestParam("id") Long subscribedToId) {
        return subscribeService.check(subscribedToId);
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

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestPart(value = "updateData", required = false) User updateData,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
    ) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            return ResponseEntity.status(401).body("unauthorized");
        }

        // If updateData is null, just skip updating other fields
        if (updateData == null) {
            updateData = new User();
        }

        return userService.updateUser(user.get().getId(), updateData, avatarFile);
    }

    @PostMapping("/profile/{reportedId}/report")
    public ResponseEntity<?> reportProfile(
            @PathVariable Long reportedId,
            @RequestBody ReportRequest request
    ) {
        return reportService.reportUser(reportedId, request.getReason());
    }

    @GetMapping("/profile/{reportedId}/report/check")
    public ResponseEntity<?> checkIfReported(@PathVariable Long reportedId) {
        return reportService.checkUserReported(reportedId);
    }

}
