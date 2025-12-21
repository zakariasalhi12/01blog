package com._blog._blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com._blog._blog.dto.ReportResponse;
import com._blog._blog.models.Post;
import com._blog._blog.models.Report;
import com._blog._blog.models.User;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.ReportRepository;
import com._blog._blog.repository.UserRepository;


@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;


    public ResponseEntity<?> reportPost(long postId , String reason) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(Username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<Post> post = postRepository.findById(postId);
        if (!post.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        if (currentUser.getId().equals(post.get().getAuthor().getId())) {
            return ResponseEntity.status(409).body("you cant report your own post");
        }

        boolean exist = reportRepository.existsByUserIdAndPostId(currentUser.getId(), postId);

        if (exist) {
            return ResponseEntity.status(400).body("already reported");
        }

        Report report = new Report(currentUser , post.get() , reason);
        reportRepository.save(report);

        return ResponseEntity.ok("Post Reported successfully");
    }

    public ResponseEntity<?> reportUser(long userId , String reason) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(Username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.status(400).body("you cant report your self");
        }

        boolean exist = reportRepository.existsByUserIdAndReportedId(currentUser.getId(), userId);

        if (exist) {
            return ResponseEntity.status(409).body("already reported");
        }

        Report report = new Report(currentUser , user.get() , reason);
        reportRepository.save(report);

        return ResponseEntity.ok("user Reported successfully");
    }

    public ResponseEntity<?> getUserReports(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report> reportsPage = reportRepository.findByUserId(currentUser.getId(), pageable);

        List<ReportResponse> reportList = reportsPage.getContent().stream()
            .map(report -> {
                String postTitle = report.getPost() != null ? report.getPost().getTitle() : null;
                Long postId = report.getPost() != null ? report.getPost().getId() : null;
                Long reportedUserId = report.getReported() != null ? report.getReported().getId() : null;
                String reportedUsername = report.getReported() != null ? report.getReported().getUsername() : null;
                
                return new ReportResponse(
                    report.getId(),
                    postId,
                    postTitle,
                    reportedUserId,
                    reportedUsername,
                    report.getReason(),
                    report.getCreatedAt(),
                    report.getUser().getUsername()
                );
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("reports", reportList);
        response.put("currentPage", reportsPage.getNumber());
        response.put("totalPages", reportsPage.getTotalPages());
        response.put("totalReports", reportsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteUserReport(long reportId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Report not found");
        }

        Report report = reportOpt.get();
        
        // User can only delete their own reports
        if (!report.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("You can only delete your own reports");
        }

        reportRepository.delete(report);
        return ResponseEntity.ok("Report deleted successfully");
    }

    public ResponseEntity<?> showReports(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report> reportsPage = reportRepository.findAll(pageable);

        List<ReportResponse> reportList = reportsPage.getContent().stream()
            .map(report -> {
                String postTitle = report.getPost() != null ? report.getPost().getTitle() : null;
                Long postId = report.getPost() != null ? report.getPost().getId() : null;
                Long reportedUserId = report.getReported() != null ? report.getReported().getId() : null;
                String reportedUsername = report.getReported() != null ? report.getReported().getUsername() : null;
                
                return new ReportResponse(
                    report.getId(),
                    postId,
                    postTitle,
                    reportedUserId,
                    reportedUsername,
                    report.getReason(),
                    report.getCreatedAt(),
                    report.getUser().getUsername()
                );
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("reports", reportList);
        response.put("currentPage", reportsPage.getNumber());
        response.put("totalPages", reportsPage.getTotalPages());
        response.put("totalReports", reportsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteReport(long reportID) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(Username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }
 
        Report report = reportRepository.findById(reportID).orElse(null);
        if (report == null) {
            return ResponseEntity.status(404).body("invalid report id");
        }

        reportRepository.delete(report);

        return ResponseEntity.ok("Report Deleted successfully");
    }
}
