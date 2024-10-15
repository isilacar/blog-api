
package com.scalefocus.blog_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
@Schema(
        description = "Tag Model Information"
)
@Builder
public record TagDto(
        @Schema(
                description = "Tag Id"
        )
        Long id,
        @Schema(
                description = "Tag Name"
        )
        String name
        ) {}

