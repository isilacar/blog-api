package com.scalefocus.blog_api.service.impl;

import com.scalefocus.blog_api.entity.Blog;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.entity.Video;
import com.scalefocus.blog_api.exception.ResourceNotFound;
import com.scalefocus.blog_api.exception.TypeNotMatchedException;
import com.scalefocus.blog_api.repository.VideoRepository;
import com.scalefocus.blog_api.response.VideoResourceResponse;
import com.scalefocus.blog_api.service.VideoService;
import com.scalefocus.blog_api.utils.BlogUtil;
import com.scalefocus.blog_api.utils.SecurityUtil;
import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ResizeResolution;
import io.github.techgnious.dto.VideoFormats;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    @Value("${file.path}")
    private String uploadDir;

    private final VideoRepository videoRepository;
    private final SecurityUtil securityUtil;
    private final BlogUtil blogUtils;

    @Override
    public String createVideo(MultipartFile file, Long blogId, Integer resolutionWidth, Integer resolutionHeight) {
        Optional<User> requestingUser = securityUtil.getRequestingUser();

        if (Objects.isNull(file.getContentType()) || !file.getContentType().startsWith("video/")) {
            throw new TypeNotMatchedException("You have to upload only videos");
        }
        try {
            Blog foundBlog = blogUtils.checkUserHasSpecificBlog(blogId, requestingUser.get());
            Path directory = Paths.get(System.getProperty("user.dir") + "/" + uploadDir);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Files.write(directory.resolve(file.getOriginalFilename()), file.getBytes());
            File savedImageFile = new File(directory + "/" + file.getOriginalFilename());

            ResizeResolution resolution = getResolution(resolutionHeight, resolutionWidth);
            VideoFormats videoFormat = getVideoFormat(file);

            //setting resolution
            IVCompressor compressor = new IVCompressor();
            compressor.reduceVideoSizeAndSaveToAPath(savedImageFile, videoFormat, resolution, uploadDir);

            Video video = new Video();
            video.setBlog(foundBlog);
            video.setName(file.getOriginalFilename());
            video.setType(file.getContentType());
            video.setFilePath(savedImageFile.getAbsolutePath());

            videoRepository.save(video);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return "Video: " + file.getOriginalFilename()+" successfully saved";
    }

    public void deleteVideo(Long videoId, Long blogId) {
        Optional<User> requestingUser = securityUtil.getRequestingUser();

        Video foundVideo = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFound("video with id: " + videoId + " is not exist"));
        boolean doesVideoHaveSpecificBlog = foundVideo.getBlog().getId().equals(blogId);
        if (!doesVideoHaveSpecificBlog) {
            throw new ResourceNotFound("video with id: " + videoId + " doesn't have specific blog with id: " + blogId);
        }

        blogUtils.checkUserHasSpecificBlog(blogId, requestingUser.get());

        Path filepath = Paths.get(foundVideo.getFilePath()).normalize();
        if (Files.exists(filepath)) {
            videoRepository.deleteById(videoId);
            try {
                Files.delete(filepath);
            } catch (IOException e) {
                throw new RuntimeException("Video with id:" + videoId + " cannot be deleted");
            }
        } else {
            throw new ResourceNotFound("File url path does not exist: " + filepath);
        }
    }

    public VideoResourceResponse getVideo(Long videoId) {
        try {
            Video foundVideo = videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFound("video with id: " + videoId + " is not exist"));
            Path filePath = Paths.get(foundVideo.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFound("Video not found with id: " + videoId);
            }
            return new VideoResourceResponse(Files.probeContentType(filePath), resource);
        } catch (IOException e) {
            throw new RuntimeException("File url path is not valid");
        }
    }

    /**
     * Setting the video resolutions with custom resolutionWidth and resolutionHeight values
     *
     * @param resolutionHeight video frame resolutionHeight
     * @param resolutionWidth  video frame resolutionWidth
     * @return new resolution value with the help of the custom inputs
     * <p>
     * Some resolutions that you can define : (resolutionWidth: 640, resolutionHeight: 480) = 480P
     *                                        (resolutionWidth: 1280,resolutionHeight: 720) = 720P
     *                                        (resolutionWidth: 1920, resolutionHeight: 1080)= 1080P
     *                                        (resolutionWidth: 2560, resolutionHeight: 1440)= 1440P
     */
    private static ResizeResolution getResolution(Integer resolutionHeight, Integer resolutionWidth) {
        ResizeResolution[] values = ResizeResolution.values();
        for (ResizeResolution resolution : values) {
            if (resolutionWidth <= resolution.getWidth() && resolutionHeight >= resolution.getHeight()) {
                return ResizeResolution.valueOf(resolution.name());
            }
        }
        return null;
    }

    /**
     * Getting the video file extension.
     *
     * @param file sending file which has to be video format
     * @return file extension such as; mp4, mov, avi
     */
    private static VideoFormats getVideoFormat(MultipartFile file) {
        String fileExtension = file.getOriginalFilename().split("\\.")[1];
        return VideoFormats.valueOf(fileExtension.toUpperCase());

    }
}
