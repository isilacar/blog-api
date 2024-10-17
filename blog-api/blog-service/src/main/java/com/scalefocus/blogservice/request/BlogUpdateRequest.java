package com.scalefocus.blogservice.request;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Blog Update Request Information"
)
public record BlogUpdateRequest(
        @Schema(
                description = "Blog Update Request title information"
        )
        String title,

        @Schema(
                description = "Blog Update Request text information"
        )
        String text) {
}
