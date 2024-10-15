package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.response.ImageResourceResponse;
import com.scalefocus.blog_api.service.ImageService;
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
        name = "CRUD REST APIs for Image Resource",
        description = "CRUD REST APIs - Create Image, Get Image, Delete Image"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "Save Image REST API",
            description = "Save Image REST API is used to save image in database and also to file"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    @PostMapping
    public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile image,
                                            @RequestParam Long blogId,
                                            @RequestParam Integer imageWidth,
                                            @RequestParam Integer imageHeight) {
        return new ResponseEntity<>(imageService.saveImage(image, blogId, imageWidth, imageHeight), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Delete Image REST API",
            description = "Delete Image REST API is used to removing images from database and file"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @DeleteMapping("/{imageId}/{blogId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId, @PathVariable Long blogId) {
        imageService.deleteImage(imageId, blogId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get Image REST API",
            description = "Get Image REST API is used to retrieving the image itself"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 OK"
    )
    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId) {
        ImageResourceResponse imageResourceResponse = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imageResourceResponse.getContentType()))
                .body(imageResourceResponse.getResource());
    }


}