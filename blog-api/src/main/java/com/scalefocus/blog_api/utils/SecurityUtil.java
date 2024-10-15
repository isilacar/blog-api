package com.scalefocus.blog_api.utils;

import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.ResourceNotFound;
import com.scalefocus.blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public Optional<User> getRequestingUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return userRepository.findByUsername(userDetails.getUsername());
            }
        } else {
            throw new ResourceNotFound("User not found");
        }
        return Optional.empty();
    }
}