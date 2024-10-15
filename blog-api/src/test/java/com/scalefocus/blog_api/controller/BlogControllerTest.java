package com.scalefocus.blog_api.controller;

import com.scalefocus.blog_api.dto.BlogDto;
import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Tag;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.request.BlogCreationRequest;
import com.scalefocus.blog_api.request.BlogUpdateRequest;
import com.scalefocus.blog_api.request.TagAddRequest;
import com.scalefocus.blog_api.response.SimplifiedBlogResponse;
import com.scalefocus.blog_api.response.UserBlogResponse;
import com.scalefocus.blog_api.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class BlogControllerTest {

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogController blogController;

    private BlogDto blogDto;
    private List<BlogDto> blogDtoList;
    private BlogUpdateRequest blogUpdateRequest;
    private TagAddRequest tagAddRequest;
    private TagDto tagAddDto;
    private List<SimplifiedBlogResponse> simplifiedBlogResponseList;
    private SimplifiedBlogResponse simplifiedBlogResponse;
    private BlogCreationRequest blogCreationReguest;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        PodamFactory podamFactory = new PodamFactoryImpl();

        user = User.builder()
                .id(1L)
                .username("test user")
                .password("test password")
                .displayName("test display name")
                .build();

        blogDto = podamFactory.manufacturePojo(BlogDto.class);
        blogDtoList = List.of(blogDto);

        blogUpdateRequest = new BlogUpdateRequest("updated title", "updated text");
        tagAddRequest = new TagAddRequest("new tag");
        tagAddDto = new TagDto(1L, tagAddRequest.tagName());
        blogDto.tagDtoSet().add(tagAddDto);
        Set<Tag> tags = blogDto.tagDtoSet().stream().map(tagDto -> new Tag()).collect(Collectors.toSet());
        blogCreationReguest = new BlogCreationRequest(blogDto.title(), blogDto.text(), tags, user.getId());
        simplifiedBlogResponse = new SimplifiedBlogResponse(blogDto.title(), blogDto.text());
        simplifiedBlogResponseList = List.of(simplifiedBlogResponse);

        UserBlogResponse userBlogResponse = podamFactory.manufacturePojo(UserBlogResponse.class);

        doReturn(blogDto).when(blogService).createBlog(any(BlogCreationRequest.class));
        doReturn(blogDtoList).when(blogService).getAllBlogs();
        doReturn(blogDto).when(blogService).addTag(anyLong(), any(TagAddRequest.class));
        doReturn(blogDto).when(blogService).removeTag(anyLong(), anyLong());
        doReturn(blogDtoList).when(blogService).getBlogsByTagName(anyString());
        doReturn(simplifiedBlogResponseList).when(blogService).getSimplifiedBlogs();
        doReturn(userBlogResponse).when(blogService).getUserBlogs(anyString());
    }

    @Test
    public void testCreatingBlog() {
        ResponseEntity<BlogDto> blogDtoResponseEntity = blogController.createBlog(blogCreationReguest);

        assertThat(blogDtoResponseEntity.getBody()).isNotNull();
        assertThat(blogDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(blogDtoResponseEntity.getBody().id()).isEqualTo(blogDto.id());
    }

    @Test
    public void testGettingAllBlogs() {
        ResponseEntity<List<BlogDto>> allBlogs = blogController.getAllBlogs();

        assertThat(allBlogs.getBody()).isNotNull();
        assertEquals(allBlogs.getStatusCode(), HttpStatusCode.valueOf(200));

    }

    @Test
    public void testGettingBlogs() {
        ResponseEntity<UserBlogResponse> userBlogs = blogController.getUserBlogs(user.getUsername());

        assertThat(userBlogs.getBody()).isNotNull();
        assertEquals(userBlogs.getStatusCode(), HttpStatusCode.valueOf(200));

    }

    @Test
    public void testUpdatingBlog() {
        blogDto = BlogDto.builder().title(blogUpdateRequest.title())
                .text(blogUpdateRequest.text()).build();

        doReturn(blogDto).when(blogService).updateBlog(anyLong(), any(BlogUpdateRequest.class));

        ResponseEntity<BlogDto> blogDtoResponseEntity = blogController.updateBlog(1L, blogUpdateRequest);

        assertThat(blogDtoResponseEntity.getBody()).isNotNull();
        assertThat(blogDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertEquals(blogDtoResponseEntity.getBody().title(), blogUpdateRequest.title());
        assertEquals(blogDtoResponseEntity.getBody().text(), blogUpdateRequest.text());

    }

    @Test
    public void testAddingTag() {
        ResponseEntity<BlogDto> blogDtoResponseEntity = blogController.addTagToBlog(1L, tagAddRequest);

        TagDto existingTag = blogDtoResponseEntity.getBody().tagDtoSet().stream().filter(tagDto -> tagDto.name().equals("new tag")).findFirst().get();

        assertThat(blogDtoResponseEntity.getBody()).isNotNull();
        assertThat(blogDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertEquals(existingTag.name(), tagAddRequest.tagName());
    }

    @Test
    public void testDeletingTag() {
        blogDto.tagDtoSet().remove(tagAddDto);

        ResponseEntity<BlogDto> blogDtoResponseEntity = blogController.deleteTagFromBlog(1L, 1L);
        boolean notExistingTag = blogDtoResponseEntity.getBody().tagDtoSet().stream().anyMatch(tagDto -> tagDto.name().equals("new tag"));

        assertThat(blogDtoResponseEntity.getBody()).isNotNull();
        assertThat(blogDtoResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertFalse(notExistingTag);
    }


    @Test
    public void testGettingAllBlogsByTagName() {

        ResponseEntity<List<BlogDto>> blogsByTagName = blogController.getAllBlogsByTagName("tag name");

        assertThat(blogsByTagName.getBody()).isNotNull();
        assertEquals(blogsByTagName.getStatusCode(), HttpStatusCode.valueOf(200));

    }

    @Test
    public void testGettingSimplifiedBlogs() {
        ResponseEntity<List<SimplifiedBlogResponse>> simplifiedBlogs = blogController.getSimplifiedBlogs();

        assertThat(simplifiedBlogs.getBody()).isNotNull();
        assertEquals(simplifiedBlogs.getStatusCode(), HttpStatusCode.valueOf(200));

    }

    @Test
    public void testDeletingBlog() {
       doNothing().when(blogService).deleteUserBlogByUsername(anyLong(),anyString());

        ResponseEntity<Void> deletedUserBlog = blogController.deleteUserBlogByUsername(1L, "username1");

        assertThat(deletedUserBlog.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));

    }

}