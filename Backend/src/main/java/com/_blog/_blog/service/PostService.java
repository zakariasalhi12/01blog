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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com._blog._blog.models.Like;
import com._blog._blog.models.Post;
import com._blog._blog.models.User;
import com._blog._blog.repository.CommentRepository;
import com._blog._blog.repository.LikeRepository;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    // ---------------- Create Post ----------------
    public ResponseEntity<?> createPost(String title, String content, MultipartFile file) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalAuthor = userRepository.findByUsername(username);
        if (!optionalAuthor.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User author = optionalAuthor.get();

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            ResponseEntity<?> fileResponse = fileStorageService.storeFile(file);

            if (!fileResponse.getStatusCode().is2xxSuccessful()) {
                return fileResponse;
            }

            Map<String, String> body = (Map<String, String>) fileResponse.getBody();
            filePath = body.get("fileName");
        }

        Post post = new Post(author, title, content, filePath);
        postRepository.save(post);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post created successfully");
        response.put("postId", post.getId());
        return ResponseEntity.ok(response);
    }

    // ---------------- Get Posts with Pagination ----------------
    public ResponseEntity<?> getPosts(int page, int size) {


        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);

        
        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            if (liked.isPresent()) {
                map.put("likedByCurrentUser", true);
            } else {
                map.put("likedByCurrentUser", false);
            }

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType = null;
                if (lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".ogg")) {
                    mediaType = "video";
                } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif") || lower.endsWith(".webp")) {
                    mediaType = "image";
                } else {
                    mediaType = "unknown";
                }
                map.put("mediaType", mediaType);
            } else {
                map.put("fileUrl", null);
                map.put("mediaType", null);
            }

            // Comment count
            long commentCount = commentRepository.countByPostId(post.getId());
            map.put("commentsCount", commentCount);

            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // ---------------- Get Logged-in User's Posts ----------------
    public ResponseEntity<List<Post>> getMyPosts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Post> posts = postRepository.findAllByAuthorUsername(username);
        return ResponseEntity.ok(posts);
    }

    // ---------------- Update Post ----------------
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
            ResponseEntity<?> fileResponse = fileStorageService.storeFile(file);
            if (!fileResponse.getStatusCode().is2xxSuccessful()) {
                return fileResponse;
            }
            Map<String, String> body = (Map<String, String>) fileResponse.getBody();
            post.setVideoOrImageUrl(body.get("fileName"));
        }

        postRepository.save(post);

        return ResponseEntity.ok(post);
    }

    // ---------------- Delete Post ----------------
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
