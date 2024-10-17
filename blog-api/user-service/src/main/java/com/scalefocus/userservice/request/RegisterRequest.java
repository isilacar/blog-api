package com.scalefocus.userservice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        description = "Register Request Information"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Schema(
            description = "Register Request username information"
    )
    private String username;

    @Schema(
            description = "Register Request password information"
    )
    private String password;

    @Schema(
            description = "Register Request display name information"
    )
    private String displayName;
}