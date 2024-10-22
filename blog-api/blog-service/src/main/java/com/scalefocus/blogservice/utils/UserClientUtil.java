package com.scalefocus.blogservice.utils;

import com.scalefocus.blogservice.dto.UserClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UserClientUtil {

    private final RestTemplate restTemplate;

    public UserClientDto getAuthenticatedUser() {
        return restTemplate.getForObject("http://user-service/api/users/getUserDetails", UserClientDto.class);
    }

    public UserClientDto findUser(Long userId) {
        return restTemplate.getForObject("http://user-service/api/users/{userId}", UserClientDto.class, userId);
    }

}
