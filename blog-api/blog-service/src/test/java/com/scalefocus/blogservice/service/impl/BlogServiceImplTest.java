package com.scalefocus.blogservice.service.impl;


import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.dto.TagDto;
import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.entity.Tag;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.mapper.BlogMapper;
import com.scalefocus.blogservice.repository.BlogRepository;
import com.scalefocus.blogservice.repository.ElasticBlogRepository;
import com.scalefocus.blogservice.repository.TagRepository;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import com.scalefocus.blogservice.request.BlogUpdateRequest;
import com.scalefocus.blogservice.request.TagAddRequest;
import com.scalefocus.blogservice.response.SimplifiedBlogResponse;
import com.scalefocus.blogservice.response.SimplifiedBlogResponsePagination;
import com.scalefocus.blogservice.response.UserBlogResponse;
import com.scalefocus.blogservice.utils.UserClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private  UserClientUtil userClientUtil;

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
    private Long userId;
    private UserClientDto userClientDto;
    private  Blog deletedBlog;
    private Page<Blog> blogPage;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;
        userClientDto = new UserClientDto(1L, "test-client-user");
        Tag tag1 = new Tag(1L, "test tag1", new HashSet<>());
        Tag tag2 = new Tag(2L, "test tag2", new HashSet<>());
        Tag tag3 = new Tag(3L, "test tag3", new HashSet<>());
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);

        blog = new Blog(1L, "test title", "test,text", tags, userId);
        blogList = List.of(blog);

        blogUpdateRequest = new BlogUpdateRequest("updated title", "updated text");
        tagAddRequest = new TagAddRequest("new tag");

        tag = Tag.builder()
                .id(TAG_ID)
                .name(tagAddRequest.tagName())
                .build();
        blog.getTags().add(tag);

        blogCreationRequest = new BlogCreationRequest(blog.getTitle(), blog.getText(), blog.getTags(), userId);
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
        blogPage = new PageImpl<>(blogList, PageRequest.of(0, 10), blogList.size());



        deletedBlog= new Blog(6L, "test title", "test,text", tags, userId);

        doReturn(blog).when(blogMapper).mapToBlog(any(BlogDto.class));
        doReturn(blogDto).when(blogMapper).mapToBlogDto(any(Blog.class));
        doReturn(blogDtoList).when(blogMapper).mapToBlogDtoList(anyList());
        doReturn(userClientDto).when(userClientUtil).getAuthenticatedUser();
        doReturn(userClientDto).when(userClientUtil).findUser(anyLong());
    }

    @Test
    public void testCreatingBlog() {

        doReturn(blog).when(blogRepository).save(any(Blog.class));
        doReturn(blog).when(blogMapper).getBlog(any(BlogCreationRequest.class), any(UserClientDto.class));

        BlogDto savedBlog = blogServiceImpl.createBlog(blogCreationRequest);

        assertThat(savedBlog).isNotNull();
        assertThat(savedBlog.id()).isEqualTo(blog.getId());

    }

    @Test
    public void testGettingAllBlogs() {
        doReturn(blogList).when(blogRepository).getBlogsByUserId(anyLong());

        UserBlogResponse userBlogResponse = blogServiceImpl.getUserBlogs(userId);

        assertThat(userBlogResponse).isNotNull();

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
        doReturn(blogPage).when(blogRepository).findAll(PageRequest.of(1, 1));

        SimplifiedBlogResponsePagination simplifiedBlogs = blogServiceImpl.getSimplifiedBlogs(1, 1);

        assertThat(simplifiedBlogs).isNotNull();
        assertFalse(simplifiedBlogs.getSimplifiedBlogResponseList().isEmpty());
        assertThat(simplifiedBlogs.getSimplifiedBlogResponseList().size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testDeletingBlog() {

        doReturn(Optional.of(deletedBlog)).when(blogRepository).findById(anyLong());
        blogServiceImpl.deleteUserBlog(6L, userId);

        verify(blogRepository, times(1)).delete(any(Blog.class));

    }

}
