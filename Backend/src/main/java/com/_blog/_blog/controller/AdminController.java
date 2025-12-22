package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.service.ReportService;
import com._blog._blog.service.UserService;
import com._blog._blog.service.PostService;

import java.util.Map;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    ReportService reportService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @DeleteMapping("/report/{reportID}")
    public ResponseEntity<?> deleteReport(
        @PathVariable long reportID
    ) {
        return reportService.deleteReport(reportID);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(
        @PathVariable Long userId,
        @RequestBody Map<String, String> request
    ) {
        String role = request.get("role");
        if (role == null || role.isEmpty()) {
            return ResponseEntity.badRequest().body("Role is required");
        }
        return userService.updateUserRole(userId, role);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size
    ) {
        return reportService.showReports(page, size);
    }

    // Posts management
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
        @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size
    ) {
        return postService.getAllPostsForAdmin(page, size);
    }

    @org.springframework.web.bind.annotation.PostMapping("/posts/{postId}/delete")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        return postService.deletePostByAdmin(postId);
    }

    @PutMapping("/posts/{postId}/visibility")
    public ResponseEntity<?> togglePostVisibility(
        @PathVariable Long postId,
        @RequestBody Map<String, Boolean> request
    ) {
        Boolean visible = request.get("visible");
        if (visible == null) {
            return ResponseEntity.badRequest().body("Visibility value is required");
        }
        return postService.togglePostVisibility(postId, visible);
    }

}
