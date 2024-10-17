package com.scalefocus.userservice.service.impl;

import com.scalefocus.userservice.dto.UserDto;
import com.scalefocus.userservice.entity.Token;
import com.scalefocus.userservice.entity.User;
import com.scalefocus.userservice.exception.ResourceNotFound;
import com.scalefocus.userservice.exception.UserExistException;
import com.scalefocus.userservice.repository.TokenRepository;
import com.scalefocus.userservice.repository.UserRepository;
import com.scalefocus.userservice.request.AuthenticationRequest;
import com.scalefocus.userservice.request.RegisterRequest;
import com.scalefocus.userservice.response.TokenResponse;
import com.scalefocus.userservice.security.JwtTokenProvider;
import com.scalefocus.userservice.service.UserService;
import com.scalefocus.userservice.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final SecurityUtil securityUtil;

    @Override
    public TokenResponse register(RegisterRequest registerRequest) {
        Boolean existsByUsername = userRepository.existsByUsername(registerRequest.getUsername());

        if (existsByUsername) {
            logger.error("Username '{}' already exists",registerRequest.getUsername());
            throw new UserExistException("Username already exists");
        }
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .displayName(registerRequest.getDisplayName())
                .build();

        User savedUser = userRepository.save(user);
        String jwtToken = jwtTokenProvider.generateToken(user);
        logger.info("Token generated successfully for the user whose id is {}", savedUser.getId());
        saveToken(savedUser, jwtToken);
        logger.info("User with user id'{}' registered successfully", savedUser.getId());
        return new TokenResponse(jwtToken);
    }


    @Override
    public TokenResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername()
                        , authenticationRequest.getPassword()));
        logger.info("Authenticated user '{}'", authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(authenticationRequest.getUsername()).get();
        logger.info("Authenticated user id '{}'",user.getId());
        String token = jwtTokenProvider.generateToken(user);
        logger.info("Token generated successfully for user with id '{}' ", user.getId());
        setExpiredAllUserTokens(user);
        saveToken(user, token);
        logger.info("Token saved successfully for user with id '{}'", user.getId());
        logger.info("User with id '{}' login successfully", user.getId());
        return new TokenResponse(token);
    }

    @Override
    public UserDto getAuthenticatedUser() {
        User user = securityUtil.getRequestingUser().get();
        return new UserDto(user.getId(), user.getUsername());
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("User with id '" + userId + "' not found"));
        return new UserDto(user.getId(), user.getUsername());
    }


    private void saveToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .isExpired(false)
                .token(jwtToken)
                .build();
        tokenRepository.save(token);
    }

    private void setExpiredAllUserTokens(User user) {
        List<Token> allNonExpiredTokens = tokenRepository.findAllNonExpiredTokens(user.getId());
        if (allNonExpiredTokens.isEmpty()) {
            return;
        }
        allNonExpiredTokens.forEach(token -> token.setExpired(true));
        tokenRepository.saveAll(allNonExpiredTokens);
    }
}