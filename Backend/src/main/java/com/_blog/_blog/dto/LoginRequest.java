package com._blog._blog.dto;

public class LoginRequest {
    private String login;
    private String password;

    // getters and setters
    public String getLogin() { return login; }
    public void setLogin(String username) { this.login = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
