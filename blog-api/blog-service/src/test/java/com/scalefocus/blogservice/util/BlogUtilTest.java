package com.scalefocus.blogservice.util;

import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.repository.BlogRepository;
import com.scalefocus.blogservice.utils.BlogUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;


public class BlogUtilTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogUtil blogUtil;

    private Blog blog;

    public static final long BLOG_ID = 1L;

    public static final String BLOG_NOT_FOUND_ERROR_MESSAGE = "Blog Not Found with id: " + BLOG_ID ;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        blog = new Blog(1L, "test title", "test text", new HashSet<>(), 1L);
        doReturn(Optional.of(blog)).when(blogRepository).findById(anyLong());
    }

    @Test
    public void testCheckUserHasSpecificBlog() {
        Blog foundedBlog = blogUtil.checkUserHasSpecificBlog(blog.getId(), blog.getUserId());

        assertThat(foundedBlog).isNotNull();
        assertThat(foundedBlog.getTitle()).isEqualTo(blog.getTitle());
        assertThat(foundedBlog.getText()).isEqualTo(blog.getText());
        assertThat(foundedBlog.getUserId()).isEqualTo(blog.getUserId());

    }

    @Test
    public void testCheckUserHasSpecificBlog_ThrowsResourceNotFoundException_WhenBlogDoesNotExist() {
        doReturn(Optional.empty()).when(blogRepository).findById(anyLong());
        ResourceNotFound assertThrows = assertThrows(ResourceNotFound.class, () -> blogUtil.checkUserHasSpecificBlog(1L,1L),
                "Should throw exception when blog not found");

        assertThat(assertThrows).hasMessage(BLOG_NOT_FOUND_ERROR_MESSAGE);

    }
}
