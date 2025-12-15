package com._blog._blog.dto;

public class FullProfileResponse {

    private String username;
    private String avatarUrl;
    private String createdAt;
    private String email;
    private int age;
    private String role; 

    public FullProfileResponse(String username, String avatarUrl, String createdAt, String email ,int age , String role) {
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.email = email;
        this.age = age;
        this.role = role;
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
