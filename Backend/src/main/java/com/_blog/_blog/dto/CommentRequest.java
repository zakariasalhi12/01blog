package com._blog._blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @NotNull(message = "postId is required")
    private Long postId;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;
}
