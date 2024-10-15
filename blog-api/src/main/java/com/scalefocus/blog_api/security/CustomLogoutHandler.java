package com.scalefocus.blog_api.security;

import com.scalefocus.blog_api.entity.Token;
import com.scalefocus.blog_api.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger logger= LogManager.getLogger(CustomLogoutHandler.class);

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.info("Logging out");
        String authHeader = request.getHeader("Authorization");
        String jwt = authHeader.substring(7);

        Token token = tokenRepository.findByToken(jwt).orElse(null);

        if (token != null) {
            token.setExpired(true);
        }
        tokenRepository.save(token);
        logger.info("Logged out successfully");
    }
}