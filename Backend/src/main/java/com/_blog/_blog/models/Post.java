package com._blog._blog.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_post_author"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    private String title;
    private String content;
    private String videoOrImageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Likes counter
    @Column(nullable = false , name = "likes_count")
    private long likesCount = 0;

    // Visibility flag (only admin can change)
    @Column(nullable = false)
    private boolean visible = true;

    public Post(User author, String title, String content, String videoOrImageUrl) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.videoOrImageUrl = videoOrImageUrl;
        this.visible = true; // Default to visible
    }

    // Helper methods
    public void incrementLikes() { this.likesCount++; }
    public void decrementLikes() { if(this.likesCount > 0) this.likesCount--; }
}
