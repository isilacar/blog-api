package com.scalefocus.blog_api.request;

import com.scalefocus.blog_api.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Schema(
        description = "Blog Creation Request Information"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogCreationRequest {
    @Schema(
            description = "Blog Title"
    )
    String title;

    @Schema(
            description = "Blog Text"
    )
    String text;

    @Schema(
            description = "Blog Tag List"
    )
    Set<Tag> tags=new HashSet<>();

    @Schema(
            description = "Blog belong to which user information"
    )
    private Long userId;
}