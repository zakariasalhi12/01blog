package com._blog._blog.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com._blog._blog.service.LikeService;
import com._blog._blog.service.PostService;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

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
            @RequestParam(defaultValue = "20") int size // page size
    ) {
        return postService.getPosts(page, size);
    }

    // GET posts by logged-in user
    @GetMapping("/posts/me")
    public ResponseEntity<?> getMyPosts() {
        return postService.getMyPosts();
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
     * Toggle like for a post.
     * If the user hasn't liked it before, it adds a like.
     * If the user already liked it, it removes the like.
     */
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId) {
        return likeService.togglePostLike(postId);
    }
}
