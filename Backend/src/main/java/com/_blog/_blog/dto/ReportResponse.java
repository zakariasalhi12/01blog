package com._blog._blog.dto;

import java.time.LocalDateTime;

public class ReportResponse {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long reportedUserId;
    private String reportedUsername;
    private String reason;
    private LocalDateTime createdAt;
    private String reporterUsername;

    public ReportResponse(Long id, Long postId, String postTitle, Long reportedUserId, 
                         String reportedUsername, String reason, LocalDateTime createdAt, String reporterUsername) {
        this.id = id;
        this.postId = postId;
        this.postTitle = postTitle;
        this.reportedUserId = reportedUserId;
        this.reportedUsername = reportedUsername;
        this.reason = reason;
        this.createdAt = createdAt;
        this.reporterUsername = reporterUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public void setReportedUsername(String reportedUsername) {
        this.reportedUsername = reportedUsername;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }
}

