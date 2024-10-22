package com.scalefocus.blogservice.exception;


public record ErrorDetails(String timestamp,
                           String errorCode,
                           String message,
                           String description) {}

