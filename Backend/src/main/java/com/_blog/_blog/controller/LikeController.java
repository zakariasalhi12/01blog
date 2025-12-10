package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog._blog.service.LikeService;

@RestController
@RequestMapping("/api")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Toggle like for a post.
     * If the user hasn't liked it before, it adds a like.
     * If the user already liked it, it removes the like.
     */
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId) {
        return likeService.togglePostLike(postId);
    }

    /**
     * Toggle like for a comment.
     * If the user hasn't liked it before, it adds a like.
     * If the user already liked it, it removes the like.
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> toggleCommentLike(@PathVariable Long commentId) {
        return likeService.toggleCommentLike(commentId);
    }
}
