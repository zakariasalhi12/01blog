package com._blog._blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com._blog._blog.dto.CommentRequest;
import com._blog._blog.models.Comment;
import com._blog._blog.models.Post;
import com._blog._blog.models.User;
import com._blog._blog.repository.CommentRepository;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // Create a comment
    public ResponseEntity<?> createComment(CommentRequest commentRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = optionalUser.get();

        Optional<Post> optionalPost = postRepository.findById(commentRequest.getPostId());
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }
        Post post = optionalPost.get();

        Comment comment = new Comment(user, post, commentRequest.getContent());
        commentRepository.save(comment);

        return ResponseEntity.ok("Comment created");
    }

    // Get paginated comments for a post, newest first
    public ResponseEntity<?> getCommentsByPost(Long postId,int page,int size) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (!optionalPost.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }
        Post post = optionalPost.get();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByPost(post, pageable);

        return ResponseEntity.ok(commentsPage);
    }

    // Delete comment with permission check
    public ResponseEntity<?> deleteComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (!optionalComment.isPresent()) {
            return ResponseEntity.status(404).body("Comment not found");
        }
        Comment comment = optionalComment.get();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = optionalUser.get();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).body("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok("Comment deleted with id: " + commentId);
    }
}
