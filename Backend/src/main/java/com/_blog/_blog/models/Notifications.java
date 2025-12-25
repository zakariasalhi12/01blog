package com._blog._blog.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "notified_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User notified;

    @Column(nullable = false)
    private boolean seen;

    @Column(length = 500, nullable = false)
    private String message;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notifications() {
    }

    public Notifications(User user, User notified, String message) {
        this.user = user;
        this.notified = notified;
        this.message = message;
        this.seen = false;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public User getNotified() {
        return notified;
    }

    public boolean getSeen() {
        return seen;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNotified(User notified) {
        this.notified = notified;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
