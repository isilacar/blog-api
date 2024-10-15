package com.scalefocus.blog_api.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(
        description = "Token Response Information"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    @Schema(
            description = "Token Response Token Information"
    )
    private String token;
}