package com.scalefocus.blog_api.service.impl;

import com.scalefocus.blog_api.dto.BlogDto;
import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Blog;
import com.scalefocus.blog_api.entity.Tag;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.ResourceNotFound;
import com.scalefocus.blog_api.mapper.BlogMapper;
import com.scalefocus.blog_api.repository.BlogRepository;
import com.scalefocus.blog_api.repository.ElasticBlogRepository;
import com.scalefocus.blog_api.repository.TagRepository;
import com.scalefocus.blog_api.repository.UserRepository;
import com.scalefocus.blog_api.request.BlogCreationRequest;
import com.scalefocus.blog_api.request.BlogUpdateRequest;
import com.scalefocus.blog_api.request.TagAddRequest;
import com.scalefocus.blog_api.response.SimplifiedBlogResponse;
import com.scalefocus.blog_api.response.UserBlogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BlogServiceImplTest {

    public static final long BLOG_ID = 1L;
    private static final Long TAG_ID = 5L;
    public static final String BLOG_NOT_FOUND_ERROR_MESSAGE = "Blog does not exist with id: " + BLOG_ID;
    public static final String BLOG_DOES_NOT_HAVE_TAG_ERROR_MESSAGE = "Blog with id:" + BLOG_ID + " does not have any tag with id: " + TAG_ID;

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private BlogMapper blogMapper;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
   private ElasticBlogRepository elasticBlogRepository;

    @InjectMocks
    private BlogServiceImpl blogServiceImpl;

    private Blog blog;
    private BlogDto blogDto;
    private List<Blog> blogList;
    private List<BlogDto> blogDtoList;
    private BlogUpdateRequest blogUpdateRequest;
    private TagAddRequest tagAddRequest;
    private Tag tag;
    private TagDto tagDto;
    private Set<TagDto> tagDtoSet;
    private BlogCreationRequest blogCreationRequest;
    private User user;
    List<User> users;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("test user")
                .password("test password")
                .displayName("test display name")
                .build();

        Tag tag1 = new Tag(1L, "test tag1", new HashSet<>());
        Tag tag2 = new Tag(2L, "test tag2", new HashSet<>());
        Tag tag3 = new Tag(3L, "test tag3", new HashSet<>());
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);

        blog = new Blog(1L, "test title", "test,text", tags, user);
        blogList = List.of(blog);

        blogUpdateRequest = new BlogUpdateRequest("updated title", "updated text");
        tagAddRequest = new TagAddRequest("new tag");

        tag = Tag.builder()
                .id(TAG_ID)
                .name(tagAddRequest.tagName())
                .build();
        blog.getTags().add(tag);

        blogCreationRequest = new BlogCreationRequest(blog.getTitle(), blog.getText(), blog.getTags(), user.getId());
        tagDtoSet = blog.getTags().stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());

        blogDto = BlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .text(blog.getText())
                .tagDtoSet(tagDtoSet)
                .build();
        blogDtoList = List.of(blogDto);

        tagDto = TagDto.builder()
                .id(TAG_ID)
                .name(tagAddRequest.tagName())
                .build();
        blogDto.tagDtoSet().add(tagDto);

        users = new ArrayList<>();
        Set<Blog> blogs = new HashSet<>(blogList);
        user.setBlogList(blogs);
        Blog newBlog = new Blog(6L, "test title", "test,text", tags, user);
        user.getBlogList().add(newBlog);
        users.add(user);

        doReturn(blog).when(blogMapper).mapToBlog(any(BlogDto.class));
        doReturn(blogDto).when(blogMapper).mapToBlogDto(any(Blog.class));
        doReturn(blogDtoList).when(blogMapper).mapToBlogDtoList(anyList());


    }

    @Test
    public void testCreatingBlog() {
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());
        doReturn(blog).when(blogRepository).save(any(Blog.class));
        doReturn(blog).when(blogMapper).getBlog(any(BlogCreationRequest.class), any(User.class));

        BlogDto savedBlog = blogServiceImpl.createBlog(blogCreationRequest);

        assertThat(savedBlog).isNotNull();
        assertThat(savedBlog.id()).isEqualTo(blog.getId());

    }

    @Test
    public void testGettingAllBlogs() {
        doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
        doReturn(blogList).when(blogRepository).getBlogsByUserUsername(anyString());

        UserBlogResponse userBlogResponse = blogServiceImpl.getUserBlogs(user.getUsername());

        assertThat(userBlogResponse).isNotNull();
        assertEquals(userBlogResponse.getDisplayName(), user.getDisplayName());

    }

    @Test
    public void testReturnUpdatedBlog_whenBlogFound() {
        blogDto = BlogDto.builder()
                .title(blogUpdateRequest.title())
                .text(blogUpdateRequest.text())
                .build();

        doReturn(Optional.ofNullable(blog)).when(blogRepository).findById(anyLong());
        doReturn(blogDto).when(blogMapper).mapToBlogDto(any(Blog.class));

        BlogDto foundBlog = blogServiceImpl.updateBlog(BLOG_ID, blogUpdateRequest);

        assertThat(foundBlog).isNotNull();
        assertEquals(foundBlog.title(), blogUpdateRequest.title());
        assertEquals(foundBlog.text(), blogUpdateRequest.text());
    }

    @Test
    public void testThrowException_whenBlogNotFound() {
        doReturn(Optional.empty()).when(blogRepository).findById(anyLong());

        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> blogServiceImpl.updateBlog(BLOG_ID, blogUpdateRequest),
                "Should throw exception when blog not found");

        assertThat(assertThrows).hasMessage(BLOG_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    public void testAddingNewTag_returnBlogDto_whenBlogFound() {
        doReturn(Optional.ofNullable(blog)).when(blogRepository).findById(anyLong());
        BlogDto foundedBlogDto = blogServiceImpl.addTag(1L, tagAddRequest);
        TagDto foundedTagDto = foundedBlogDto.tagDtoSet().stream().filter(tagDto -> tagDto.name().equals("new tag")).findFirst().get();

        assertThat(foundedBlogDto).isNotNull();
        assertEquals(foundedTagDto.name(), tagAddRequest.tagName());

    }


    @Test
    public void testAddingNewTag_ThrowException_whenBlogNotFound() {
        doReturn(Optional.empty()).when(blogRepository).findById(anyLong());
        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> blogServiceImpl.addTag(BLOG_ID, tagAddRequest),
                "Should throw exception when blog not found");
        assertThat(assertThrows).hasMessage(BLOG_NOT_FOUND_ERROR_MESSAGE);

    }

    @Test
    public void testRemovingTag_returnBlogDto_whenBlogAndTagFound() {
        blogDto.tagDtoSet().remove(tagDto);
        doReturn(Optional.ofNullable(blog)).when(blogRepository).findById(anyLong());

        BlogDto foundedBlogDto = blogServiceImpl.removeTag(BLOG_ID, TAG_ID);

        boolean notExistingTag = foundedBlogDto.tagDtoSet().stream().anyMatch(tagDto -> tagDto.name().equals("new tag"));

        assertThat(foundedBlogDto).isNotNull();
        assertFalse(notExistingTag);

    }

    @Test
    public void testRemovingTag_throwException_whenBlogNotFound() {
        doReturn(Optional.empty()).when(blogRepository).findById(anyLong());
        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> blogServiceImpl.removeTag(BLOG_ID, TAG_ID),
                "Should throw exception when blog not found");
        assertThat(assertThrows).hasMessage(BLOG_NOT_FOUND_ERROR_MESSAGE);

    }

    @Test
    public void testRemovingTag_throwException_whenBlogDoesNotHaveSpecificTag() {
        blog.getTags().remove(tag);
        doReturn(Optional.ofNullable(blog)).when(blogRepository).findById(anyLong());

        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> blogServiceImpl.removeTag(BLOG_ID, TAG_ID),
                "Should throw exception when blog does not have specific tag");

        assertThat(assertThrows).hasMessage(BLOG_DOES_NOT_HAVE_TAG_ERROR_MESSAGE);
    }

    @Test
    public void testGettingBlogsByTagName() {
        doReturn(blogList).when(blogRepository).findByTagsName(anyString());

        List<BlogDto> blogsByTagName = blogServiceImpl.getBlogsByTagName(tagAddRequest.tagName());

        assertThat(blogsByTagName).isNotNull();
        assertFalse(blogsByTagName.isEmpty());
        assertThat(blogsByTagName.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGettingSimplifiedBlogs() {
        doReturn(blogList).when(blogRepository).findAll();

        List<SimplifiedBlogResponse> simplifiedBlogs = blogServiceImpl.getSimplifiedBlogs();

        assertThat(simplifiedBlogs).isNotNull();
        assertFalse(simplifiedBlogs.isEmpty());
        assertThat(simplifiedBlogs.size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testDeletingBlog() {
        doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
        doReturn(user).when(userRepository).save(any(User.class));

        blogServiceImpl.deleteUserBlogByUsername(6L, user.getUsername());

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(blogRepository, times(1)).delete(any(Blog.class));

    }

}
