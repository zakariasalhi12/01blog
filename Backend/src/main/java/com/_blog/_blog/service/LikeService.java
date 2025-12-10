package com._blog._blog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com._blog._blog.models.Comment;
import com._blog._blog.models.Like;
import com._blog._blog.models.Post;
import com._blog._blog.models.User;
import com._blog._blog.repository.CommentRepository;
import com._blog._blog.repository.LikeRepository;
import com._blog._blog.repository.PostRepository;
import com._blog._blog.repository.UserRepository;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Toggle like for post
    public ResponseEntity<?> togglePostLike(Long postId) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return ResponseEntity.status(404).body("Post not found");

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // User already liked -> remove like
            likeRepository.delete(existingLike.get());
            post.decrementLikes();
            postRepository.save(post);
            return ResponseEntity.ok("Like removed");
        } else {
            // User has not liked yet -> add like
            Like like = new Like(user, post, true);
            likeRepository.save(like);
            post.incrementLikes();
            postRepository.save(post);
            return ResponseEntity.ok("Like added");
        }
    }

    // Toggle like for comment
    public ResponseEntity<?> toggleCommentLike(Long commentId) {
        User user = getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return ResponseEntity.status(404).body("Comment not found");

        Optional<Like> existingLike = likeRepository.findByUserAndComment(user, comment);

        if (existingLike.isPresent()) {
            // User already liked -> remove like
            likeRepository.delete(existingLike.get());
            comment.decrementLikes();
            commentRepository.save(comment);
            return ResponseEntity.ok("Like removed");
        } else {
            // User has not liked yet -> add like
            Like like = new Like(user, comment, true);
            likeRepository.save(like);
            comment.incrementLikes();
            commentRepository.save(comment);
            return ResponseEntity.ok("Like added");
        }
    }
}
