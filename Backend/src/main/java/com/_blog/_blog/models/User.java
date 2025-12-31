package com._blog._blog.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    public enum Role {
        USER,
        ADMIN,
        BANNED
    }

    // @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-increment
    private Long id;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private  String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    
    private String avatar;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public User(String username, String email, String password, int age) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.avatar = "/uploads/profile.jpg"; // default avatar
    }
}
