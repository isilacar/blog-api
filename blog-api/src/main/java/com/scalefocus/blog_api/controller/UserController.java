package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.request.AuthenticationRequest;
import com.scalefocus.blog_api.request.RegisterRequest;
import com.scalefocus.blog_api.response.TokenResponse;
import com.scalefocus.blog_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(
        name = "CRUD REST APIs for User Resource",
        description = "CRUD REST APIs - Create User, Login User"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger= LogManager.getLogger(UserController.class);

    private final UserService userService;

    @Operation(
            summary = "Create User REST API",
            description = "Create User REST API is used to save user in a database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("Registering new user with name '{}'", registerRequest.getUsername());
        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login User REST API",
            description = "Login User REST API is used to login to the system for existing user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        logger.info("User '{}' is trying to login to the system", authenticationRequest.getUsername());
        return new ResponseEntity<>(userService.login(authenticationRequest), HttpStatus.OK);
    }
}