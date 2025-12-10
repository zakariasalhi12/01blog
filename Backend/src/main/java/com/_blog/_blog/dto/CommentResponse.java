package com._blog._blog.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private long likes;
    private Long userId;
    private String username;

    public CommentResponse(Long commentId, String content, LocalDateTime createdAt, Long userId, String username ,long likes) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.likes = likes;
    }

    // Getters
    public Long getCommentId() { return commentId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public long getLikes() { return likes; }
}
