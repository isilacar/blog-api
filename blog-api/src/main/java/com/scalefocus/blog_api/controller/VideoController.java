package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.response.VideoResourceResponse;
import com.scalefocus.blog_api.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "CRUD REST APIs for Video Resource",
        description = "CRUD REST APIs - Create Video, Delete Video, Get Video"
)
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @Operation(
            summary = "Create Video REST API",
            description = "Create Video REST API is used to save video in a database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping
    public ResponseEntity<String> createVideo(@RequestParam("video") MultipartFile file,
                              @RequestParam Long blogId,
                              @RequestParam Integer resolutionWidth,
                              @RequestParam Integer resolutionHeight) {

        return new ResponseEntity<>( videoService.createVideo(file, blogId, resolutionWidth, resolutionHeight),HttpStatus.CREATED);
    }

    @Operation(
            summary = "Delete Video REST API",
            description = "Delete Video REST API is used to save user in a database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @DeleteMapping("/{videoId}/{blogId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long videoId, @PathVariable Long blogId) {
        videoService.deleteVideo(videoId, blogId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get Video REST API",
            description = "Get Video REST API is used to get the video from database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/{videoId}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long videoId) {
        VideoResourceResponse resourceResponse = videoService.getVideo(videoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceResponse.getContentType()))
                .body(resourceResponse.getResource());
    }
}
