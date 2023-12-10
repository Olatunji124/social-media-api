package com.assessment.socialmedia.usecases.exceptions;


public class RequestForbiddenException extends RuntimeException {
    public RequestForbiddenException(String message){
        super(message);
    }
}
