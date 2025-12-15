package com._blog._blog.dto;

public class FullProfileResponse {

    private String username;
    private String avatarUrl;
    private String createdAt;
    private String email;
    private int age;
    private String role; 
    private long subscribers;
    private long subscriptions;

    public FullProfileResponse(String username, String avatarUrl, String createdAt, String email ,int age , String role , long subscribers , long subscriptions) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.email = email;
        this.age = age;
        this.role = role;
        this.subscriptions = subscriptions;
        this.subscribers = subscribers;

    }

    public long getSubscribers() {
        return subscribers;
    }

    public long getSubscriptions() {
        return subscriptions;
    }

    public int getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
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
