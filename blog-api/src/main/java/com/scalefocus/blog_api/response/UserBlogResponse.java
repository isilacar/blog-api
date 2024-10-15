package com.scalefocus.blog_api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
        description = "User Blog Response Information "
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBlogResponse {

    @Schema(
            description = "User Display Name Information "
    )
    private String displayName;

    @Schema(
            description = "Blog List that belong to user information"
    )
    private List<BlogResponse> blogs;
}