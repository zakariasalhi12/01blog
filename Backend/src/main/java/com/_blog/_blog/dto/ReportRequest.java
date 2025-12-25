package com._blog._blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReportRequest {

    @NotBlank(message = "Reason is required")
    @Size(min = 3, max = 1000, message = "Reason must be between 3 and 1000 characters")
    private String reason;

    public ReportRequest() {}

    public ReportRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}
