package com.scalefocus.blogservice.service;


import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import com.scalefocus.blogservice.request.BlogUpdateRequest;
import com.scalefocus.blogservice.request.TagAddRequest;
import com.scalefocus.blogservice.response.SimplifiedBlogResponse;
import com.scalefocus.blogservice.response.SimplifiedBlogResponsePagination;
import com.scalefocus.blogservice.response.UserBlogResponse;

import java.util.List;

public interface BlogService {

    BlogDto createBlog(BlogCreationRequest blogCreationRequest);

    List<BlogDto> getAllBlogs();

    UserBlogResponse getUserBlogs(Long userId);

    BlogDto updateBlog(Long blogId, BlogUpdateRequest blogUpdateRequest);

    BlogDto addTag(Long blogId, TagAddRequest tagAddRequest);

    BlogDto removeTag(Long blogId, Long tagId);

    List<BlogDto> getBlogsByTagName(String tagName);

    SimplifiedBlogResponsePagination getSimplifiedBlogs(int pageNumber, int pageSize);

    void deleteUserBlog(Long blogId,Long userId);

    List<ElasticBlogDocument> searchByKeyword (String keyword);

}
