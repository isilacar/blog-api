package com.scalefocus.blog_api.service;

import com.scalefocus.blog_api.request.AuthenticationRequest;
import com.scalefocus.blog_api.request.RegisterRequest;
import com.scalefocus.blog_api.response.TokenResponse;

public interface UserService {
    TokenResponse register(RegisterRequest registerRequest);

    TokenResponse login(AuthenticationRequest authenticationRequest);
}