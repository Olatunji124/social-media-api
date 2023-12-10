package com.assessment.socialmedia.usecases.data.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppToken {
    private String token;
    private int expiryTimeInMinutes;
}
