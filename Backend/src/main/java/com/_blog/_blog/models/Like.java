package com._blog._blog.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes", 
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id", "comment_id"})})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private boolean isLike;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Like() {}

    // For post
    public Like(User user, Post post, boolean isLike) {
        this.user = user;
        this.post = post;
        this.isLike = isLike;
    }

    // For comment
    public Like(User user, Comment comment, boolean isLike) {
        this.user = user;
        this.comment = comment;
        this.isLike = isLike;
    }

    // Getters and setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }
    public boolean isLike() { return isLike; }
    public void setLike(boolean isLike) { this.isLike = isLike; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
