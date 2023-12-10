package com.assessment.socialmedia.usecases.exceptions;


public class UnauthorisedAccessException extends RuntimeException {
    public UnauthorisedAccessException(String message){
        super(message);
    }
}
