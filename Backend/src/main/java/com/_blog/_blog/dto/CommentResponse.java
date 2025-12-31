package com._blog._blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponse {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private long likes;
    private Long userId;
    private String username;
    private String avatar;
    private boolean owner;

    public CommentResponse(Long commentId, String content, LocalDateTime createdAt, Long userId, String username ,long likes , String avatar , boolean owner) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.likes = likes;
        this.avatar =avatar;
        this.owner = owner;
    }
}
