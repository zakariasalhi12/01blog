package com._blog._blog.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Likes counter
    @Column(nullable = false)
    private long likesCount = 0;

    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }

    // Helper methods
    public void incrementLikes() { this.likesCount++; }
    public void decrementLikes() { if(this.likesCount > 0) this.likesCount--; }
}
