package com.scalefocus.blog_api.utils;


import com.scalefocus.blog_api.entity.Blog;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.ResourceNotFound;
import org.springframework.stereotype.Component;

@Component
public class BlogUtil {

    public Blog checkUserHasSpecificBlog(Long blogId, User authenticatedUser) {
        return authenticatedUser.getBlogList().stream().filter(blog -> blog.getId().equals(blogId)).findFirst()
                .orElseThrow(() -> new ResourceNotFound("User with id: " + authenticatedUser.getId() +
                        " does not have any blog with id: " + blogId));

    }
}
