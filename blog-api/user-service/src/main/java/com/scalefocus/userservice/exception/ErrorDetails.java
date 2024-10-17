package com.scalefocus.userservice.exception;


public record ErrorDetails(String timestamp,
                           String errorCode,
                           String message,
                           String description) {}

