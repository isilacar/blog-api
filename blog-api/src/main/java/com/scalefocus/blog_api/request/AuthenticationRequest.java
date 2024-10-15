package com.scalefocus.blog_api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(
        description = "Authentication Request Information"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @Schema(
            description = "Authenticated username"
    )
    private String username;

    @Schema(
            description = "Authenticated password"
    )
    private String password;
}