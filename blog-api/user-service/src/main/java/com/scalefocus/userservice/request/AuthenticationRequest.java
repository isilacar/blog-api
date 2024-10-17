package com.scalefocus.userservice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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