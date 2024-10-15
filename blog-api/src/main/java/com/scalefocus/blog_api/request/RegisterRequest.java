package com.scalefocus.blog_api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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