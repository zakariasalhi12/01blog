package com._blog._blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
            return ResponseEntity.status(400).body("you cant report your own post");
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
            return ResponseEntity.status(400).body("already reported");
        }

        Report report = new Report(currentUser , user.get() , reason);
        reportRepository.save(report);

        return ResponseEntity.ok("user Reported successfully");
    }

    public ResponseEntity<?> showReports(int page, int size) {
        return ResponseEntity.ok("good");
    }

    public ResponseEntity<?> deleteReport(long reportID) {
        String Username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(Username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!"ADMIN".equals(currentUser.getRole().toString())) {
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
