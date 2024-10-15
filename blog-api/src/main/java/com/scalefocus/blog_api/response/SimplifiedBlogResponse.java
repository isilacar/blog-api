package com.scalefocus.blog_api.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Simplified Blog Response Information "
)
public record SimplifiedBlogResponse(
        @Schema(
                description = "Simplified Blog Response title information"
        )
        String title,

        @Schema(
                description = "Simplified Blog Response text information"
        )
        String text) {
}
