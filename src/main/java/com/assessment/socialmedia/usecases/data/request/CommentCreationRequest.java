package com.assessment.socialmedia.usecases.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreationRequest {
    @NotNull
    @NotBlank(message = "content is required.")
    private String content;

    @NotNull(message = "post Id is required.")
    private Long postId;
}
