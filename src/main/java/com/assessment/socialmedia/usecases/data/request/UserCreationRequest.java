package com.assessment.socialmedia.usecases.data.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserCreationRequest {
    @NotNull
    @NotBlank(message = "username is required.")
    private String username;

    @NotNull
    @NotBlank(message = "password is required.")
    @Size(min = 8)
    private String password;

    @NotNull
    @NotBlank(message = "email is required.")
    @Email()
    private String email;

    @NotNull(message = "Profile picture is required.")
    private MultipartFile profilePicture;
}
