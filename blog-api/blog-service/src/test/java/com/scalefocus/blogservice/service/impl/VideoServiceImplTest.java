package com.scalefocus.blogservice.service.impl;

import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.entity.Video;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.repository.VideoRepository;
import com.scalefocus.blogservice.response.VideoResourceResponse;
import com.scalefocus.blogservice.utils.BlogUtil;
import com.scalefocus.blogservice.utils.UserClientUtil;
import io.github.techgnious.exception.VideoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class VideoServiceImplTest {

    private static final Long VIDEO_ID = 1L;
    private static final Long BLOG_DOES_NOT_EXIST = 2L;
    private static final String VIDEO_DOES_NOT_HAVE_SPECIFIC_BLOG_MESSAGE = "video with id: " + VIDEO_ID + " doesn't have specific blog with id: " + BLOG_DOES_NOT_EXIST;
    private static final String VIDEO_DOES_NOT_EXIST_MESSAGE = "video with id: " + VIDEO_ID + " is not exist";
    private static final String FILE_NOT_FOUND = "Video not found with id: " + VIDEO_ID;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private BlogUtil blogUtils;

    @Mock
    private UserClientUtil userClientUtil;

    @InjectMocks
    private VideoServiceImpl videoServiceImpl;

    private Long userId;
    private Blog blog;
    private MockMultipartFile multipartFile;
    private Video video;
    private MockedStatic<Paths> pathsMocked;
    private Path directory;
    private UserClientDto userClientDto;

    @BeforeEach
    public void setUp() throws IOException, VideoException {
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
        Files.createDirectories(directory);
        multipartFile = new MockMultipartFile("video", "test.mp4", "video/mp4", "content".getBytes());
        video = new Video(1L, blog, multipartFile.getOriginalFilename(), multipartFile.getContentType(), directory + "/" + multipartFile.getOriginalFilename());
        pathsMocked = Mockito.mockStatic(Paths.class);

        doReturn(userClientDto).when(userClientUtil).getAuthenticatedUser();
        when(Paths.get(anyString())).thenReturn(directory);
        doReturn(blog).when(blogUtils).checkUserHasSpecificBlog(anyLong(), anyLong());
        doReturn(Optional.of(video)).when(videoRepository).findById(anyLong());

    }

    @AfterEach
    public void tearDown() {
        pathsMocked.close();
    }

    @Test
    public void testGettingVideo() throws IOException {
        Path testPath = directory.resolve("test.mp4");
        Files.createFile(testPath);

        when(Paths.get(anyString())).thenReturn(testPath);

        VideoResourceResponse videoResourceResponse = videoServiceImpl.getVideo(1L);
        assertThat(videoResourceResponse).isNotNull();
        assertEquals("video/mp4", videoResourceResponse.getContentType());
    }

    @Test
    public void testGettingVideo_throwException_whenVideoDoesNotExist() {
        doReturn(Optional.empty()).when(videoRepository).findById(anyLong());
        ResourceNotFound imageIdNotFound = assertThrows(ResourceNotFound.class, () -> videoServiceImpl.getVideo(1L), "Video id does not exist");

        assertEquals(VIDEO_DOES_NOT_EXIST_MESSAGE, imageIdNotFound.getMessage());
    }

    @Test
    public void testGettingVideo_throwException_whenResourceNotAvailable() {
        ResourceNotFound fileNotFound = assertThrows(ResourceNotFound.class, () -> videoServiceImpl.getVideo(1L), "File not found");

        assertEquals(FILE_NOT_FOUND, fileNotFound.getMessage());
    }

    @Test
    public void testDeletingVideo() {
        videoServiceImpl.deleteVideo(1L, 1L);

        verify(videoRepository, times(1)).deleteById(any(Long.class));
        verify(videoRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testDeletingVideo_throwException_whenBlogDoesNotExist() {
        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> videoServiceImpl.deleteVideo(1L, 2L),
                "Deleting image does not have specific blog");

        assertThat(assertThrows).hasMessage(VIDEO_DOES_NOT_HAVE_SPECIFIC_BLOG_MESSAGE);
    }

}
