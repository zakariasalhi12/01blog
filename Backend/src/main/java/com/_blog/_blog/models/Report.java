package com._blog._blog.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "reported_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User reported;

    @Column(length = 500 , nullable = false)
    private String reason;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Report(User user , Post post , String reason) {
        this.user = user;
        this.post = post;
        this.reason = reason;
    }

    public Report(User user , User reported , String reason) {
        this.user = user;
        this.reported = reported;
        this.reason = reason;
    }
}
