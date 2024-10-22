package com.scalefocus.blogservice.service.impl;

import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.exception.UserNotAuthenticatedException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

public class AuthorizationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            String authorization = requestAttributes.getRequest().getHeader("Authorization");
            request.getHeaders().add("Authorization", authorization);
            response = execution.execute(request, body);

            if (!(response.getStatusCode().is2xxSuccessful())) {
                if (request.getURI().getPath().equals("/api/users/getUserDetails")) {
                    throw new UserNotAuthenticatedException("User not authenticated");
                }
                throw new ResourceNotFound("User not found");

            }
            return response;
        }
        return null;
    }
}