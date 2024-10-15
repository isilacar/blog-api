package com.scalefocus.blog_api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        description = "Blog Response Information "
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogResponse {

    @Schema(
            description = "Blog Title Information"
    )
    String title;

    @Schema(
            description = "Blog Text Information"
    )
    String text;
}