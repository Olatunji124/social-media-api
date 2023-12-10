package com.assessment.socialmedia.usecases.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    @NotBlank(message = "username is required.")
    private String username;

    @NotNull
    @NotBlank(message = "Password is required.")
    private String password;
}
