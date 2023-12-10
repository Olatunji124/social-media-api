package com.assessment.socialmedia.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseJSON<T> {

    public ApiResponseJSON(String message) {
        this.message = message;
        this.data = null;
    }
    public ApiResponseJSON(String message, T data) {
        this.message = message;
        this.data = data;
    }

    private String message;
    private T data;
}
