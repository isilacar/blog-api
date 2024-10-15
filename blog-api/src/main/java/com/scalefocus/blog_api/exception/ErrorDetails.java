package com.scalefocus.blog_api.exception;


public record ErrorDetails(String timestamp,
                           String errorCode,
                           String message,
                           String description) {}

