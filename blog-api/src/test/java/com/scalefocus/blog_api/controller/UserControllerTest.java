package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.request.AuthenticationRequest;
import com.scalefocus.blog_api.request.RegisterRequest;
import com.scalefocus.blog_api.response.TokenResponse;
import com.scalefocus.blog_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    RegisterRequest registerRequest;
    TokenResponse tokenResponse;
    AuthenticationRequest authenticationRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setPassword("password");
        registerRequest.setDisplayName("displayName");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(registerRequest.getUsername());
        authenticationRequest.setPassword(registerRequest.getPassword());

        tokenResponse = new TokenResponse();
        tokenResponse.setToken("token taken");
    }

    @Test
    public void testRegisterUser() {
        doReturn(tokenResponse).when(userService).register(any(RegisterRequest.class));

        ResponseEntity<TokenResponse> register = userController.register(registerRequest);

        assertThat(register.getBody()).isNotNull();
        assertThat(register.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(register.getBody().getToken()).isEqualTo("token taken");
    }

    @Test
    public void testLoginUser() {
        doReturn(tokenResponse).when(userService).login(authenticationRequest);

        ResponseEntity<TokenResponse> login = userController.login(authenticationRequest);

        assertThat(login.getBody()).isNotNull();
        assertThat(login.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(login.getBody().getToken()).isEqualTo("token taken");
    }
}