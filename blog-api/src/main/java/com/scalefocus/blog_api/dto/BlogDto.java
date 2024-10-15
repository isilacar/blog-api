package com.scalefocus.blog_api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

@Schema(
        description = "BlogDto Model Information"
)
@Builder
public record BlogDto(
        @Schema(
                description = "Blog Id"
        )
        Long id,
        @Schema(
                description = "Blog Title"
        )
        String title,
        @Schema(
                description = "Blog Text"
        )
        String text,
        @Schema(
                description = "Blog Tag List"
        )
        Set<TagDto> tagDtoSet

) {}
