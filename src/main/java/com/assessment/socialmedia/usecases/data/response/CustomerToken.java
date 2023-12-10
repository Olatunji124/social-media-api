package com.assessment.socialmedia.usecases.data.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerToken {
    private AppToken accessToken;
    private AppToken refreshToken;
}
