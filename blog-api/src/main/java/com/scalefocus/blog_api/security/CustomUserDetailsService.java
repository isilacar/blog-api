package com.scalefocus.blog_api.security;

import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.UsernameNotFoundException;
import com.scalefocus.blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger= LogManager.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found", username);
                    return new UsernameNotFoundException("User not exist with username: " + username);
                });

        logger.info("Authenticated User id '{}' has found", user.getId());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new HashSet<>()
        );
    }
}