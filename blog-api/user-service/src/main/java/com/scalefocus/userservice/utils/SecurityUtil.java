package com.scalefocus.userservice.utils;


import com.scalefocus.userservice.entity.User;
import com.scalefocus.userservice.exception.ResourceNotFound;
import com.scalefocus.userservice.repository.UserRepository;
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
            if (!(principal instanceof UserDetails userDetails)) {
                throw new ResourceNotFound("User not found");
            }
            return userRepository.findByUsername(userDetails.getUsername());
        }
        return Optional.empty();
    }
}