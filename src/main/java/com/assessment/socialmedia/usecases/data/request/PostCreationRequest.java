package com.assessment.socialmedia.usecases.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostCreationRequest {
    @NotNull
    @NotBlank(message = "content is required.")
    private String content;
}
