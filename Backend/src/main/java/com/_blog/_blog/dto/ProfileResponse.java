package com._blog._blog.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private String username;
    private String avatarUrl;
    private String createdAt;
    private long subscribers;
    private long subscriptions;
}
