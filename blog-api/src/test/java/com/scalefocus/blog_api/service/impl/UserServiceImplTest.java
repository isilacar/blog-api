package com.scalefocus.blog_api.service.impl;

import com.scalefocus.blog_api.entity.Token;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.UserExistException;
import com.scalefocus.blog_api.repository.TokenRepository;
import com.scalefocus.blog_api.repository.UserRepository;
import com.scalefocus.blog_api.request.AuthenticationRequest;
import com.scalefocus.blog_api.request.RegisterRequest;
import com.scalefocus.blog_api.response.TokenResponse;
import com.scalefocus.blog_api.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

public class UserServiceImplTest {

    public static final String USERNAME_ALREADY_EXISTS_ERROR_MESSAGE = "Username already exists";
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private RegisterRequest registerRequest;
    private String jwtToken;
    private AuthenticationRequest authenticationRequest;
    private UsernamePasswordAuthenticationToken authenticationToken;
    private Token token;
    private List<Token> tokenList;
    String encodedPassword;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setPassword("password");
        registerRequest.setDisplayName("displayName");

        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setDisplayName("displayName");

        encodedPassword = passwordEncoder.encode(user.getPassword());

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(user.getUsername());
        authenticationRequest.setPassword(user.getPassword());

        jwtToken = "test.token.created";

        token = new Token();
        token.setId(1L);
        token.setToken(jwtToken);
        token.setUser(user);
        token.setExpired(false);

        tokenList = new ArrayList<>();
        tokenList.add(token);

        authenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername()
                , authenticationRequest.getPassword());
    }

    @Test
    public void testRegisterUser_returnSuccess_whenUserNotExistInDb() {
        doReturn(false).when(userRepository).existsByUsername(anyString());
        doReturn(encodedPassword).when(passwordEncoder).encode(any(String.class));
        doReturn(user).when(userRepository).save(any(User.class));
        doReturn(jwtToken).when(jwtTokenProvider).generateToken(any(User.class));
        doReturn(token).when(tokenRepository).save(any(Token.class));

        TokenResponse tokenResponse = userServiceImpl.register(registerRequest);

        assertThat(tokenResponse).isNotNull();
        assertThat(token.getToken()).isEqualTo(jwtToken);
    }

    @Test
    public void testRegisterUser_throwException_whenUserExistInDb() {
        doReturn(true).when(userRepository).existsByUsername(anyString());

        UserExistException userExistException = assertThrows(UserExistException.class, () -> userServiceImpl.register(registerRequest),
                "Should throw UserExistException when user exists is in DB");

        assertThat(userExistException).hasMessage(USERNAME_ALREADY_EXISTS_ERROR_MESSAGE);
    }

    @Test
    public void testAuthenticateUser() {
        token.setExpired(true);

        doReturn(authenticationToken).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        doReturn(Optional.of(user)).when(userRepository).findByUsername(authenticationRequest.getUsername());
        doReturn(jwtToken).when(jwtTokenProvider).generateToken(any(User.class));
        doReturn(tokenList).when(tokenRepository).saveAll(anyList());

        TokenResponse userLoginToken = userServiceImpl.login(authenticationRequest);

        assertThat(userLoginToken).isNotNull();
        assertThat(userLoginToken.getToken()).isNotNull();
        assertThat(userLoginToken.getToken()).isEqualTo(jwtToken);
    }
}