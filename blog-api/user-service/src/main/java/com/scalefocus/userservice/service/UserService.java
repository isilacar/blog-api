package com.scalefocus.userservice.service;


import com.scalefocus.userservice.dto.UserDto;
import com.scalefocus.userservice.request.AuthenticationRequest;
import com.scalefocus.userservice.request.RegisterRequest;
import com.scalefocus.userservice.response.TokenResponse;

public interface UserService {
    TokenResponse register(RegisterRequest registerRequest);

    TokenResponse login(AuthenticationRequest authenticationRequest);

    UserDto getAuthenticatedUser();

    UserDto findById(Long userId);
}