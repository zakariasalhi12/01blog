package com._blog._blog.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}
