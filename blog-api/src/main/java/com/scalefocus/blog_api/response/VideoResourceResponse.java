package com.scalefocus.blog_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoResourceResponse {
    private String contentType;
    private Resource resource;
}
