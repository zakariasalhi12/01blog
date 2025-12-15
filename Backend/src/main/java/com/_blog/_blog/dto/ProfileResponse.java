package com._blog._blog.dto;

import org.springframework.cglib.core.Local;

public class ProfileResponse {
    private String username;
    private String avatarUrl;
    private String createdAt;
    private long subscribers;
    private long subscriptions;

    public ProfileResponse(String username, String avatarUrl, String createdAt, long subscribers , long subscriptions) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.subscribers = subscribers;
        this.subscriptions = subscriptions;
    }


    public long getSubscribers() {
        return subscribers;
    }

    public long getSubscriptions() {
        return subscriptions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
