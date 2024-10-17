package com.scalefocus.blogservice.service;

import com.scalefocus.blogservice.response.VideoResourceResponse;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {

    String createVideo(MultipartFile file, Long blogId, Integer resolutionWidth, Integer resolutionHeight);

    void deleteVideo(Long videoId, Long blogId);

    VideoResourceResponse getVideo(Long videoId);
}
