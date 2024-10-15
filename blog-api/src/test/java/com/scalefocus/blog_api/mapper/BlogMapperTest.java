package com.scalefocus.blog_api.mapper;

import com.scalefocus.blog_api.dto.BlogDto;
import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Blog;
import com.scalefocus.blog_api.entity.Tag;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.request.BlogCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;

public class BlogMapperTest {

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private BlogMapper blogMapper;

    private Blog blog;
    private BlogDto blogDto;
    private Set<Tag> tags;
    private Set<TagDto> tagDtoSet;
    private List<Blog> blogList;
    private BlogCreationRequest blogCreationRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user=User.builder()
                .id(1L)
                .username("test user")
                .password("test password")
                .displayName("test display name")
                .build();

        Tag tag1=new Tag(1L,"test tag1",new HashSet<>());
        Tag tag2=new Tag(2L,"test tag2",new HashSet<>());
        Tag tag3=new Tag(3L,"test tag3",new HashSet<>());
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);

        blog=new Blog(1L,"test title","test,text",tags,user);
        tags = blog.getTags();
        tagDtoSet = tags.stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName()))
                .collect(Collectors.toSet());

        blogList=List.of(blog);

        blogDto = BlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .text(blog.getText())
                .tagDtoSet(tagDtoSet)
                .build();

        blogCreationRequest=BlogCreationRequest.builder()
                .title(blog.getTitle())
                .text(blog.getText())
                .tags(blog.getTags())
                .build();

        doReturn(tagDtoSet).when(tagMapper).mapToTagDtoList(anySet());

        doReturn(tags).when(tagMapper).mapToTagList(anySet());


    }

    @Test
    public void testMapToBlogDto() {
        BlogDto returnedBlogDto = blogMapper.mapToBlogDto(blog);

        assertThat(returnedBlogDto).isNotNull();
        assertThat(returnedBlogDto.title()).isEqualTo(blogDto.title());
        assertThat(returnedBlogDto.tagDtoSet().size()).isEqualTo(tagDtoSet.size());
        assertThat(returnedBlogDto.id()).isEqualTo(blog.getId());

    }

    @Test
    public void testMapToBlog() {
        Blog returnedBlog = blogMapper.mapToBlog(blogDto);

        assertThat(returnedBlog).isNotNull();
        assertThat(returnedBlog.getTitle()).isEqualTo(blog.getTitle());
        assertThat(returnedBlog.getId()).isEqualTo(blog.getId());
        assertThat(returnedBlog.getTags()).isEqualTo(blog.getTags());
    }

    @Test
    public void testMapToBlogDtoList(){
        List<BlogDto> blogDtoList = blogMapper.mapToBlogDtoList(blogList);

        assertThat(blogDtoList).isNotNull();
        assertEquals(blogDtoList.size(),blogList.size());
        assertEquals(blogDtoList.get(0).id(),blogList.get(0).getId());

    }

    @Test
    public void testBlogCreationRequestToBlog(){
        Blog returnedBlog = blogMapper.getBlog(blogCreationRequest, user);

        assertThat(returnedBlog).isNotNull();
        assertThat(returnedBlog.getTitle()).isEqualTo(blogCreationRequest.getTitle());
        assertThat(returnedBlog.getTags()).isEqualTo(blogCreationRequest.getTags());
    }
}