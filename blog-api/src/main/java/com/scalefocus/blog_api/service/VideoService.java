package com.scalefocus.blog_api.service;

import com.scalefocus.blog_api.response.VideoResourceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    String createVideo(MultipartFile file, Long blogId, Integer resolutionWidth, Integer resolutionHeight);

    void deleteVideo(Long videoId, Long blogId);

    VideoResourceResponse getVideo(Long videoId);
}
