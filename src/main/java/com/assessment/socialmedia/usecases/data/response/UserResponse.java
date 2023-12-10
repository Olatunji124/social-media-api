package com.assessment.socialmedia.usecases.data.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String username;
    private String email;
    private byte[] profilePicture;
}
