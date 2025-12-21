package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.dto.ReportRequest;
import com._blog._blog.service.LikeService;
import com._blog._blog.service.PostService;
import com._blog._blog.service.ReportService;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ReportService reportService;

    // CREATE a post
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return postService.createPost(title, content, file);
    }

    // GET all posts
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(defaultValue = "0") int page, // page number, 0-indexed
            @RequestParam(defaultValue = "20") int size, // page size
            @RequestParam(value = "id", required = false, defaultValue = "0") long id
    ) {
        return postService.getPosts(id, page, size);
    }

    // GET posts by logged-in user
    @GetMapping("/posts/me")
    public ResponseEntity<?> myPosts(
            @RequestParam(defaultValue = "0") int page, // page number, 0-indexed
            @RequestParam(defaultValue = "20") int size // page size
    ) {
        return postService.getMyPosts(page, size);
    }

    @GetMapping("/posts/user/{id}")
    public ResponseEntity<?> getPostsByuser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, // page number, 0-indexed
            @RequestParam(defaultValue = "20") int size // page size
    ) {
        return postService.userPost(id, page, size);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        return postService.updatePost(id, title, content, file);
    }

    // DELETE a post
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    /**
     * Toggle like for a post. If the user hasn't liked it before, it adds a
     * like. If the user already liked it, it removes the like.
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId) {
        return likeService.togglePostLike(postId);
    }

    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<?> reportProfile(
            @PathVariable Long postId,
            @RequestBody ReportRequest request
    ) {
        return reportService.reportPost(postId, request.getReason());
    }

    @GetMapping("/posts/reports")
    public ResponseEntity<?> getUserReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return reportService.getUserReports(page, size);
    }

    @DeleteMapping("/posts/reports/{reportId}")
    public ResponseEntity<?> deleteUserReport(@PathVariable Long reportId) {
        return reportService.deleteUserReport(reportId);
    }
}
