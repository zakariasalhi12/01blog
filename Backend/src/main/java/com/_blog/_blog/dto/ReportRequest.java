package com._blog._blog.dto;

public class ReportRequest {
    public String reason;

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
