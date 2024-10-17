package com.scalefocus.blogservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResourceResponse {
    private String contentType;
    private Resource resource;
}