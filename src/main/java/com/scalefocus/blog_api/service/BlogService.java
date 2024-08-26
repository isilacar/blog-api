package com.scalefocus.blog_api.service;


import com.scalefocus.blog_api.dto.BlogDto;
import com.scalefocus.blog_api.request.BlogUpdateRequest;
import com.scalefocus.blog_api.request.TagAddRequest;
import com.scalefocus.blog_api.response.SimplifiedBlogResponse;

import java.util.List;

public interface BlogService {

    BlogDto createBlog(BlogDto blogDto);

    List<BlogDto> getAllBlogs();

    BlogDto updateBlog(Long blogId, BlogUpdateRequest blogUpdateRequest);

    BlogDto addTag(Long blogId, TagAddRequest tagAddRequest);

    BlogDto removeTag(Long blogId, Long tagId);

    List<BlogDto> getBlogsByTagName(String tagName);

    List<SimplifiedBlogResponse> getSimplifiedBlogs();
}
