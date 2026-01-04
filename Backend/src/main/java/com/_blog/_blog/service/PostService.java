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
import com._blog._blog.models.Subscriptions;
import com._blog._blog.models.User;
import com._blog._blog.repository.CommentRepository;
import com._blog._blog.repository.LikeRepository;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.SubscriptionsRepository;
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

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Autowired 
    NotificationService notificationService;

    // ---------------- Create Post ----------------
    @SuppressWarnings("null")
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

            @SuppressWarnings("unchecked")
            Map<String, String> body = (Map<String, String>) fileResponse.getBody();
            filePath = body.get("fileName");
        }

        Post post = new Post(author, title, content, filePath);
        postRepository.save(post);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post created successfully");
        response.put("postId", post.getId());


        List<Subscriptions> subscriptions = subscriptionsRepository.findAllBySubscribedToId_Id(author.getId());
        subscriptions.forEach(sub -> {
            User subscriber = sub.getSubscriber();
            String message = author.getUsername() + " published a new post ";
            notificationService.createNotification(subscriber.getId(), message);
        });

        return ResponseEntity.ok(response);
    }

    // ---------------- Get Posts with Pagination ----------------
    public ResponseEntity<?> getPosts(long id, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // If id is provided, return the single post
        if (id != 0) {
            Optional<Post> postOpt = postRepository.findById(id);
            if (postOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Post not found");
            }
            Post post = postOpt.get();

            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            map.put("authorId", post.getAuthor().getId());
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            map.put("likedByCurrentUser", liked.isPresent());

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType;
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
            map.put("owner", username.equals(post.getAuthor().getUsername()));

            return ResponseEntity.ok(map);
        }

        // Otherwise return paginated list of posts
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Filter by visibility (only visible posts for regular users, all for admin)
        Page<Post> postsPage;
        if (currentUser.getRole().equals(User.Role.ADMIN)) {
            postsPage = postRepository.findAll(pageable);
        } else {
            postsPage = postRepository.findAllByVisibleTrue(pageable);
        }

        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            map.put("authorId", post.getAuthor().getId());
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            map.put("likedByCurrentUser", liked.isPresent());

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType;
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

            long commentCount = commentRepository.countByPostId(post.getId());
            map.put("commentsCount", commentCount);
            map.put("owner", username.equals(post.getAuthor().getUsername()));

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
    public ResponseEntity<?> getMyPosts(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // For regular users, do not include hidden posts in their own /posts/me view.
        Page<Post> postsPage;
        if (currentUser.getRole().equals(User.Role.ADMIN)) {
            postsPage = postRepository.findAllByAuthorUsername(username, pageable);
        } else {
            postsPage = postRepository.findAllByAuthorUsernameAndVisibleTrue(username, pageable);
        }

        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            map.put("authorId", post.getAuthor().getId());
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            map.put("likedByCurrentUser", liked.isPresent());

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType;
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

            long commentCount = commentRepository.countByPostId(post.getId());
            map.put("commentsCount", commentCount);
            map.put("owner", username.equals(post.getAuthor().getUsername()));

            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> userPost(long id, int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("user not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Only return posts that are visible (exclude hidden posts) when fetching another user's posts
        Page<Post> postsPage = postRepository.findAllByAuthorUsernameAndVisibleTrue(user.getUsername(), pageable);

        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            map.put("authorId", post.getAuthor().getId());
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            map.put("likedByCurrentUser", liked.isPresent());

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType;
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

            long commentCount = commentRepository.countByPostId(post.getId());
            map.put("commentsCount", commentCount);
            map.put("owner", username.equals(post.getAuthor().getUsername()));

            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // ---------------- Update Post ----------------
    @SuppressWarnings("null")
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
            @SuppressWarnings("unchecked")
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

    // ---------------- Admin: Get All Posts ----------------
    public ResponseEntity<?> getAllPostsForAdmin(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        
        if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postsPage = postRepository.findAll(pageable);

        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("authorId", post.getAuthor().getId());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("visible", post.isVisible());
            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    // ---------------- Admin: Delete Post ----------------
    public ResponseEntity<?> deletePostByAdmin(Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        
        if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        postRepository.delete(optionalPost.get());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ---------------- Admin: Toggle Post Visibility ----------------
    public ResponseEntity<?> togglePostVisibility(Long postId, boolean visible) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        
        if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.status(403).body("Forbidden: Admin access required");
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        Post post = optionalPost.get();
        post.setVisible(visible);
        postRepository.save(post);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Post visibility updated successfully");
        return ResponseEntity.ok(response);
    }
    // ---------------- Get Subscribed Posts ----------------
    public ResponseEntity<?> getSubscribedPosts(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Subscriptions> subs = subscriptionsRepository.findAllBySubscriberId_Id(currentUser.getId());
        List<Long> authorIds = subs.stream()
                .map(sub -> sub.getSubscribedTo().getId())
                .toList();

        if (authorIds.isEmpty()) {
             // Return empty page if no subscriptions
             Map<String, Object> response = new HashMap<>();
             response.put("posts", List.of());
             response.put("currentPage", 0);
             response.put("totalPages", 0);
             response.put("totalPosts", 0);
             return ResponseEntity.ok(response);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Using existing repository method
        Page<Post> postsPage = postRepository.findAllByAuthorIdInAndVisibleTrue(authorIds, pageable);

        List<Map<String, Object>> postsList = postsPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("content", post.getContent());
            map.put("author", post.getAuthor().getUsername());
            map.put("createdAt", post.getCreatedAt());
            map.put("likesCount", post.getLikesCount());
            map.put("avatar", post.getAuthor().getAvatar());
            map.put("authorId", post.getAuthor().getId());
            Optional<Like> liked = likeRepository.findByUserAndPost(currentUser, post);
            map.put("likedByCurrentUser", liked.isPresent());

            // File URL & media type
            if (post.getVideoOrImageUrl() != null) {
                String fileUrl = "/uploads/" + post.getVideoOrImageUrl();
                map.put("fileUrl", fileUrl);

                String lower = post.getVideoOrImageUrl().toLowerCase();
                String mediaType;
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

            long commentCount = commentRepository.countByPostId(post.getId());
            map.put("commentsCount", commentCount);
            map.put("owner", username.equals(post.getAuthor().getUsername()));

            return map;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsList);
        response.put("currentPage", postsPage.getNumber());
        response.put("totalPages", postsPage.getTotalPages());
        response.put("totalPosts", postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
}
