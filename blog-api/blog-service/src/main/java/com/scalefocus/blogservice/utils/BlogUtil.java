package com.scalefocus.blogservice.utils;


import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogUtil {

    private final BlogRepository blogRepository;

    public Blog checkUserHasSpecificBlog(Long blogId, Long userId) {
        Blog foundBlog = blogRepository.findById(blogId).orElseThrow(() -> new ResourceNotFound("Blog Not Found with id: " + blogId));
        if (foundBlog.getUserId().equals(userId)) {
            return foundBlog;
        } else {
            throw new ResourceNotFound("User does not have specific blog");
        }
    }
}
