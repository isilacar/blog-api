package com.scalefocus.blogservice.service.impl;


import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.entity.Image;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.repository.ImageRepository;
import com.scalefocus.blogservice.response.ImageResourceResponse;
import com.scalefocus.blogservice.utils.BlogUtil;
import com.scalefocus.blogservice.utils.UserClientUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class ImageServiceImplTest {

    private static final Long IMAGE_ID = 1L;
    private static final Long BLOG_DOES_NOT_EXIST = 2L;
    private static final String IMAGE_DOES_NOT_HAVE_SPECIFIC_BLOG_MESSAGE = "image with id: " + IMAGE_ID + " doesn't have specific blog with id: " + BLOG_DOES_NOT_EXIST;
    private static final String IMAGE_DOES_NOT_EXIST_MESSAGE = "image: " + IMAGE_ID + " is not exist";
    private static final String FILE_NOT_FOUND = "File not found with imageId: " + IMAGE_ID;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private BlogUtil blogUtils;

    @Mock
    private UserClientUtil userClientUtil;

    @InjectMocks
    private ImageServiceImpl imageServiceImpl;

    private Long userId;
    private Blog blog;
    private MultipartFile multipartFile;
    private Image image;
    private MockedStatic<ImageIO> imageIOMocked;
    private MockedStatic<Paths> pathsMocked;
    private Path directory;
    private UserClientDto userClientDto;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        userClientDto = new UserClientDto(1L, "test-client-user");

        blog = Blog.builder()
                .id(1L)
                .title("test title")
                .text("test,text")
                .userId(userId)
                .build();

        directory = Files.createTempDirectory("test-uploads");
        multipartFile = new MockMultipartFile("file", "test.jpeg", MediaType.IMAGE_JPEG_VALUE, "test.jpeg".getBytes());
        image = new Image(1L, blog, multipartFile.getOriginalFilename(), multipartFile.getContentType(), directory + "/" + multipartFile.getOriginalFilename());
        imageIOMocked = Mockito.mockStatic(ImageIO.class);
        pathsMocked = Mockito.mockStatic(Paths.class);

        doReturn(userClientDto).when(userClientUtil).getAuthenticatedUser();
        when(Paths.get(anyString())).thenReturn(directory);
        doReturn(blog).when(blogUtils).checkUserHasSpecificBlog(anyLong(), anyLong());

        when(ImageIO.read(any(InputStream.class))).thenAnswer(invocation -> {
            BufferedImage result = new BufferedImage(100, 120, BufferedImage.TYPE_INT_RGB); // create a BufferedImage object
            return result;
        });
        doReturn(Optional.of(image)).when(imageRepository).findById(anyLong());


    }

    @AfterEach
    public void tearDown() {
        imageIOMocked.close();
        pathsMocked.close();
    }

    @Test
    public void testGettingImage() throws IOException {
        Path testPath = directory.resolve("test.jpeg");
        Files.createFile(testPath);

        when(Paths.get(anyString())).thenReturn(testPath);

        ImageResourceResponse imageResource = imageServiceImpl.getImage(1L);
        assertThat(imageResource).isNotNull();
    }

    @Test
    public void testGettingImage_throwException_whenImageDoesNotExist() throws IOException {
        doReturn(Optional.empty()).when(imageRepository).findById(anyLong());
        ResourceNotFound imageIdNotFound = assertThrows(ResourceNotFound.class, () -> imageServiceImpl.getImage(1L), "Image id does not exist");

        assertEquals(IMAGE_DOES_NOT_EXIST_MESSAGE, imageIdNotFound.getMessage());
    }

    @Test
    public void testGettingImage_throwException_whenResourceNotAvailable() throws IOException {
        ResourceNotFound fileNotFound = assertThrows(ResourceNotFound.class, () -> imageServiceImpl.getImage(1L), "File not found");

        assertEquals(FILE_NOT_FOUND, fileNotFound.getMessage());
    }

    @Test
    public void testDeletingImage() {
        imageServiceImpl.deleteImage(1L, 1L);

        verify(imageRepository, times(1)).delete(any(Image.class));
        verify(imageRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testDeletingImage_throwException_whenBlogDoesNotExist() {
        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> imageServiceImpl.deleteImage(1L, 2L),
                "Deleting image does not have specific blog");

        assertThat(assertThrows).hasMessage(IMAGE_DOES_NOT_HAVE_SPECIFIC_BLOG_MESSAGE);
    }

    @Test
    public void testUploadingImage() {
        String saveImageResponse = imageServiceImpl.saveImage(multipartFile, blog.getId(), 100, 120);

        assertThat(saveImageResponse).isNotNull();
        assertEquals("Image file uploaded successfully: " + multipartFile.getOriginalFilename(), saveImageResponse);

    }

}