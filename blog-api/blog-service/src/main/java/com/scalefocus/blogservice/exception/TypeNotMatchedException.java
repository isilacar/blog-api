package com.scalefocus.blogservice.exception;

public class TypeNotMatchedException extends RuntimeException {
    public TypeNotMatchedException(String message) {
        super(message);
    }
}