package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.response.VideoResourceResponse;
import com.scalefocus.blog_api.service.impl.VideoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class VideoControllerTest {

    @Mock
    private VideoServiceImpl videoServiceImpl;

    @InjectMocks
    private VideoController videoController;

    private MultipartFile multipartFile;
    private VideoResourceResponse videoResourceResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        multipartFile = new MockMultipartFile("video", "test.mp4", "video/mp4", "content".getBytes());
        videoResourceResponse=new VideoResourceResponse(multipartFile.getContentType(), multipartFile.getResource());

        doReturn(uploadingVideoResponse()).when(videoServiceImpl).createVideo(any(MultipartFile.class),anyLong(),anyInt(),anyInt());
        doNothing().when(videoServiceImpl).deleteVideo(anyLong(),anyLong());
        doReturn(videoResourceResponse).when(videoServiceImpl).getVideo(anyLong());

    }

    @Test
    public void testUploadingVideo() {

        ResponseEntity<String> uploadVideoResponse = videoController.createVideo(multipartFile, 1L, 640, 480);

        assertThat(uploadVideoResponse.getBody()).isNotNull();
        assertThat(uploadVideoResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(uploadVideoResponse.getBody()).isEqualTo(uploadingVideoResponse());
    }

    @Test
    public void testDeletingVideo(){
        ResponseEntity<Void> deleteResponse = videoController.deleteVideo(1L, 1L);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }

    @Test
    public void testGettingVideo(){
        ResponseEntity<Resource> getVideoResponse = videoController.getVideo(1L);

        assertThat(getVideoResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(getVideoResponse.getBody()).isNotNull();
        assertThat(getVideoResponse.getBody().getFilename()).isEqualTo("test.mp4");

    }

    private String uploadingVideoResponse() {
        return "Video: " + multipartFile.getOriginalFilename()+" successfully saved";
    }

}