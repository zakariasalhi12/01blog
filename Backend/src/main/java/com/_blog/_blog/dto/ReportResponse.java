package com._blog._blog.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private Long postId;
    private String postTitle;
    private Long reportedUserId;
    private String reportedUsername;
    private String reason;
    private LocalDateTime createdAt;
    private String reporterUsername;

}
