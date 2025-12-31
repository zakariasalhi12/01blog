package com._blog._blog.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "post_id", "comment_id"})})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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
}
