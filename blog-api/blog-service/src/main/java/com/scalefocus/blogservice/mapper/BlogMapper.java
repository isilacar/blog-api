package com.scalefocus.blogservice.mapper;

import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlogMapper {

    private final TagMapper tagMapper;

    public BlogDto mapToBlogDto(Blog blog) {
        return BlogDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .text(blog.getText())
                .tagDtoSet(tagMapper.mapToTagDtoList(blog.getTags()))
                .build();
    }

    public Blog mapToBlog(BlogDto blogDto) {
       return Blog.builder().id(blogDto.id())
                .title(blogDto.title())
                .text(blogDto.text())
                .tags(tagMapper.mapToTagList(blogDto.tagDtoSet()))
                .build();

    }
    public Blog getBlog(BlogCreationRequest blogCreationRequest, UserClientDto user) {
        return Blog.builder()
                .title(blogCreationRequest.getTitle())
                .text(blogCreationRequest.getText())
                .userId(user.getId())
                .tags(blogCreationRequest.getTags())
                .build();
    }
    public List<BlogDto> mapToBlogDtoList(List<Blog> blogs) {
        return blogs.stream().map(this::mapToBlogDto).toList();
    }

}
