package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.response.ImageResourceResponse;
import com.scalefocus.blog_api.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class ImageControllerTest {
    
    @Mock
    private ImageServiceImpl imageServiceImpl;

    @InjectMocks
    private ImageController imageController;

    private MultipartFile multipartFile;
    private ImageResourceResponse imageResourceResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        multipartFile=new MockMultipartFile("file","test.jpeg", MediaType.IMAGE_JPEG_VALUE, "test.jpeg".getBytes());
        imageResourceResponse=new ImageResourceResponse(multipartFile.getContentType(), multipartFile.getResource());

        doReturn(uploadingImageResponse()).when(imageServiceImpl).saveImage(any(MultipartFile.class),anyLong(),anyInt(),anyInt());
        doNothing().when(imageServiceImpl).deleteImage(anyLong(),anyLong());
        doReturn(imageResourceResponse).when(imageServiceImpl).getImage(anyLong());

    }

    @Test
    public void testUploadingImage() {
 
        ResponseEntity<String> uploadImageResponse = imageController.saveImage(multipartFile, 1L, 120, 100);

        assertThat(uploadImageResponse.getBody()).isNotNull();
        assertThat(uploadImageResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(uploadImageResponse.getBody()).isEqualTo(uploadingImageResponse());
    }

    @Test
    public void testDeletingImage(){
        ResponseEntity<Void> deleteResponse = imageController.deleteImage(1L, 1L);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
    }


    @Test
    public void testGettingImage(){
        ResponseEntity<Resource> getImageResponse = imageController.getImage(1L);

        assertThat(getImageResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(getImageResponse.getBody()).isNotNull();
        assertThat(getImageResponse.getBody().getFilename()).isEqualTo("test.jpeg");

    }

    private String uploadingImageResponse() {
        return "Image file uploaded successfully: " + multipartFile.getOriginalFilename();
    }

}