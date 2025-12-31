package com._blog._blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportRequest {

    @NotBlank(message = "Reason is required")
    @Size(min = 3, max = 1000, message = "Reason must be between 3 and 1000 characters")
    private String reason;

}
