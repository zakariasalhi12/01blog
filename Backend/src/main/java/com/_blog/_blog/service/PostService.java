package com._blog._blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.models.Post;
import com._blog._blog.models.User;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.UserRepository;
import com._blog._blog.service.FileStorageService;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    // Create post
    public ResponseEntity<?> createPost(String title,String content, MultipartFile file) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalAuthor = userRepository.findByUsername(username);
        if (!optionalAuthor.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User author = optionalAuthor.get();

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.storeFile(file);
        }

        Post post = new Post(author, title, content, filePath);
        postRepository.save(post);

        return ResponseEntity.ok("Post created successfully");
    }

    // Get posts with pagination
    public ResponseEntity<?> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);

        // Optionally, convert the posts to include full file URLs
        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            if (post.getVideoOrImageUrl() != null) {
                map.put("fileUrl", "/uploads/" + post.getVideoOrImageUrl());
            } else {
                map.put("fileUrl", null);
            }
            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // Get posts by logged-in user
    public ResponseEntity<List<Post>> getMyPosts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Post> posts = postRepository.findAllByAuthorUsername(username);
        return ResponseEntity.ok(posts);
    }

    // Update post
    public ResponseEntity<?> updatePost(Long id, String title, String content, MultipartFile file) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Post> optionalPost = postRepository.findById(id);
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        Post post = optionalPost.get();

        if (!post.getAuthor().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("You are not allowed to update this post");
        }

        post.setTitle(title);
        post.setContent(content);

        if (file != null && !file.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(file);
            post.setVideoOrImageUrl(storedFileName);
        }

        postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    // Delete post
    public ResponseEntity<?> deletePost(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Post> optionalPost = postRepository.findById(id);
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        Post post = optionalPost.get();

        if (!post.getAuthor().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("You are not allowed to delete this post");
        }

        postRepository.delete(post);
        return ResponseEntity.ok("Post deleted successfully");
    }
}
