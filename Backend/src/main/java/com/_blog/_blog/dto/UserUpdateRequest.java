package com._blog._blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    // Username is not allowed to change; if provided it will be rejected by service
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email too long")
    private String email;

    @Size(min = 6, max = 128, message = "Password must be at least 6 characters")
    private String password;

    @Min(value = 0, message = "Age must be a non-negative number")
    private Integer age;

    public UserUpdateRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
