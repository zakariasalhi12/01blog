package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.dto.CommentRequest;
import com._blog._blog.dto.LoginRequest;
import com._blog._blog.service.CommentService;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // create a comment
    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
        return commentService.createComment(commentRequest);
    }

    // get comments 
    @GetMapping("/comments")
    public ResponseEntity<?> getCommentsByPost(
        @RequestParam("postId") Long postId,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size) {
        return commentService.getCommentsByPost(postId, page, size);
    }

    // delete comment
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

}
