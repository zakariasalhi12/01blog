package com._blog._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserListResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
}

