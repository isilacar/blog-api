package com.scalefocus.userservice.exception;

public class ResourceNotFound extends RuntimeException {

    public ResourceNotFound(String message) {
        super(message);
    }
}
