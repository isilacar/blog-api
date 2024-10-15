package com.scalefocus.blog_api.exception;

public class TypeNotMatchedException extends RuntimeException {
    public TypeNotMatchedException(String message) {
        super(message);
    }
}