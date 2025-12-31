package com._blog._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FullProfileResponse {
    private String username;
    private String avatarUrl;
    private String createdAt;
    private String email;
    private int age;
    private String role; 
    private long subscribers;
    private long subscriptions;
}
