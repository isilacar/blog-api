package com.scalefocus.blog_api.request;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Tag Add Request Information"
)
public record TagAddRequest(
        @Schema(
                description = "Tag Add Request tag name information"
        )
        String tagName) {
}
