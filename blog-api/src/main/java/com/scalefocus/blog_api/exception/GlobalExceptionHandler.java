package com.scalefocus.blog_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorDetails> resourceNotFoundException(ResourceNotFound resourceNotFound,
                                                                  WebRequest webRequest) {

        return new ResponseEntity<>(new ErrorDetails(getTime()
                , "RESOURCE NOT FOUND"
                , resourceNotFound.getMessage(),
                webRequest.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ErrorDetails> badCredentialsException(UserExistException userExistException,
                                                                WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorDetails(getTime()
                , "BAD_REQUEST"
                , userExistException.getMessage(),
                webRequest.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDetails> badCredentialsException(UsernameNotFoundException usernameNotFoundException,
                                                                WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorDetails(getTime()
                , "BAD_REQUEST"
                , usernameNotFoundException.getMessage()
                , webRequest.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TypeNotMatchedException.class)
    public ResponseEntity<ErrorDetails> typeMissMatchedException(TypeNotMatchedException typeMismatchException, WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorDetails(getTime(),
                "BAD_REQUEST",
                typeMismatchException.getMessage(),
                webRequest.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> generalRuntimeException(RuntimeException runtimeException, WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorDetails(getTime(),
                "BAD_REQUEST",
                runtimeException.getMessage(),
                webRequest.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(dateTimeFormatter);
    }
}
