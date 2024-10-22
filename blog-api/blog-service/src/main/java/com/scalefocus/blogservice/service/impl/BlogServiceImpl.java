package com.scalefocus.blogservice.service.impl;


import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.Blog;
import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import com.scalefocus.blogservice.entity.ElasticTag;
import com.scalefocus.blogservice.entity.Tag;
import com.scalefocus.blogservice.exception.ResourceNotFound;
import com.scalefocus.blogservice.mapper.BlogMapper;
import com.scalefocus.blogservice.repository.BlogRepository;
import com.scalefocus.blogservice.repository.ElasticBlogRepository;
import com.scalefocus.blogservice.repository.TagRepository;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import com.scalefocus.blogservice.request.BlogUpdateRequest;
import com.scalefocus.blogservice.request.TagAddRequest;
import com.scalefocus.blogservice.response.BlogResponse;
import com.scalefocus.blogservice.response.SimplifiedBlogResponse;
import com.scalefocus.blogservice.response.SimplifiedBlogResponsePagination;
import com.scalefocus.blogservice.response.UserBlogResponse;
import com.scalefocus.blogservice.service.BlogService;
import com.scalefocus.blogservice.utils.UserClientUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private static final Logger logger = LogManager.getLogger(BlogServiceImpl.class);

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;
    private final TagRepository tagRepository;
    private final UserClientUtil userClientUtil;
    private final ElasticBlogRepository elasticBlogRepository;
    private final RestTemplate restTemplate;

    @Override
    public BlogDto createBlog(BlogCreationRequest blogCreationRequest) {
        UserClientDto user = userClientUtil.getAuthenticatedUser();

        logger.info("User with id '{}' has founded", blogCreationRequest.getUserId());
        Blog blog = blogMapper.getBlog(blogCreationRequest, user);
        Blog savedBlog = blogRepository.save(blog);

        ElasticBlogDocument elasticBlogDocument = new ElasticBlogDocument();
        elasticBlogDocument.setId(savedBlog.getId());
        elasticBlogDocument.setTitle(savedBlog.getTitle());
        elasticBlogDocument.setText(savedBlog.getText());
        elasticBlogDocument.setUserId(user.getId());
        elasticBlogDocument.setTags(blog.getTags().stream()
                .map(tag -> new ElasticTag(tag.getId(), tag.getName())).toList());

        elasticBlogRepository.save(elasticBlogDocument);


        logger.info("Blog has created successfully by the user id '{}'", user.getId());
        return blogMapper.mapToBlogDto(savedBlog);
    }

    @Override
    public List<BlogDto> getAllBlogs() {
        logger.info("Getting all blogs from the database");
        return blogMapper.mapToBlogDtoList(blogRepository.findAll());
    }

    @Override
    public BlogDto updateBlog(Long blogId, BlogUpdateRequest blogUpdateRequest) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    logger.error("Blog does not exist with id '{}'", blogId);
                    return new ResourceNotFound("Blog does not exist with id: " + blogId);
                });
        UserClientDto user = userClientUtil.getAuthenticatedUser();
        if (blog.getUserId() != user.getId()) {
            logger.error("User with id '{}' has not any blog with id {}", blog.getUserId(), blogId);
            throw new ResourceNotFound("Blog with id: " + blogId + " does not belong to user with id: " + user.getId());
        }

        logger.info("Blog has found with id '{}'", blogId);
        blog.setTitle(blogUpdateRequest.title());
        blog.setText(blogUpdateRequest.text());

        blogRepository.save(blog);
        logger.info("Blog with id '{}' has updated successfully", blogId);
        return blogMapper.mapToBlogDto(blog);
    }

    @Override
    public BlogDto addTag(Long blogId, TagAddRequest tagAddRequest) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    logger.error("Blog does not found with id '{}'", blogId);
                    return new ResourceNotFound("Blog does not exist with id: " + blogId);
                });

        UserClientDto user = userClientUtil.getAuthenticatedUser();
        if (blog.getUserId() != user.getId()) {
            logger.error("User with id '{}' has not any blog with id {}", blog.getUserId(), blogId);
            throw new ResourceNotFound("Blog with id: " + blogId + " does not belong to user with id: " + user.getId());
        }

        logger.info("Blog has found with id '{}'", blogId);
        Tag tag = Tag.builder().name(tagAddRequest.tagName()).build();
        blog.getTags().add(tag);
        blogRepository.save(blog);
        logger.info("New tag has added successfully to the blog with id '{}'", blogId);
        return blogMapper.mapToBlogDto(blog);
    }

    @Override
    public BlogDto removeTag(Long blogId, Long tagId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> {
                    logger.error("Blog does not exist with id '{}'", blogId);
                    return new ResourceNotFound("Blog does not exist with id: " + blogId);
                });

        UserClientDto user = userClientUtil.getAuthenticatedUser();
        if (blog.getUserId() != user.getId()) {
            logger.error("User with id '{}' has not any blog with id {}", blog.getUserId(), blogId);
            throw new ResourceNotFound("Blog with id: " + blogId + " does not belong to user with id: " + user.getId());
        }

        logger.info("Blog with id '{}' has found", blogId);
        Tag tag = blog.getTags().stream()
                .filter(t -> t.getId().equals(tagId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Blog with id '{}' does not have any tag with id '{}'", blogId, tagId);
                    return new ResourceNotFound("Blog with id:" + blogId + " does not have any tag with id: " + tagId);
                });
        logger.info("Tag with id '{}' has found", tagId);

        blog.getTags().remove(tag);
        blogRepository.save(blog);
        tagRepository.deleteById(tagId);

        logger.info("Tag with id '{}' has removed successfully from the blog with id '{}'", tagId, blogId);
        return blogMapper.mapToBlogDto(blog);
    }

    @Override
    public List<BlogDto> getBlogsByTagName(String tagName) {
        List<Blog> blogs = blogRepository.findByTagsName(tagName);
        logger.info("Getting all blogs with specified tag name '{}'", tagName);
        return blogMapper.mapToBlogDtoList(blogs);
    }

    @Override
    public SimplifiedBlogResponsePagination getSimplifiedBlogs(int pageNumber, int pageSize) {
        Page<Blog> blogRepositoryPagination = blogRepository.findAll(PageRequest.of(pageNumber, pageSize));
        logger.info("Getting all simplified blogs with pagination");

        SimplifiedBlogResponsePagination responsePagination = new SimplifiedBlogResponsePagination();
        responsePagination.setSimplifiedBlogResponseList(blogRepositoryPagination.getContent().stream().map(data -> new SimplifiedBlogResponse(data.getTitle(), data.getText())).toList());
        responsePagination.setTotalValue(blogRepositoryPagination.getTotalElements());
        responsePagination.setTotalPages(blogRepositoryPagination.getTotalPages());
        responsePagination.setCurrentPage(blogRepositoryPagination.getPageable().getPageNumber());
        responsePagination.setViewedValueCount(blogRepositoryPagination.getPageable().getPageSize());

        return responsePagination;
    }

    @Override
    public void deleteUserBlog(Long blogId, Long userId) {
        UserClientDto user = userClientUtil.getAuthenticatedUser();

        logger.info("User has found with user id '{}'", user.getId());
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new ResourceNotFound("Blog does not exist with id: " + blogId));
        if (user.getId().equals(blog.getUserId())) {
            logger.info("Blog has found with id '{}'", blogId);
            blogRepository.delete(blog);
            logger.info("Blog with id '{}' has deleted successfully which belongs to user with id '{}'", blogId, user.getId());
        } else {
            throw new ResourceNotFound("Blog does not belong to this user with id: " + user.getId());
        }

    }

    @Override
    public List<ElasticBlogDocument> searchByKeyword(String keyword) {
        userClientUtil.getAuthenticatedUser();
        return elasticBlogRepository.searchByKeyword(keyword);
    }

    public UserBlogResponse getUserBlogs(Long userId) {
        UserClientDto authenticatedUser = userClientUtil.getAuthenticatedUser();

        UserClientDto user = userClientUtil.findUser(userId);
        if (Objects.isNull(user)) {
            throw new ResourceNotFound("User does not exist with id: " + userId);
        }

        logger.info("User has found with user id '{}'", user.getId());
        List<Blog> userBlogs = blogRepository.getBlogsByUserId(authenticatedUser.getId());
        List<BlogResponse> blogResponseList = userBlogs.stream().map(blog -> new BlogResponse(blog.getTitle(), blog.getText())).toList();

        UserBlogResponse userBlogResponse = new UserBlogResponse();
        userBlogResponse.setUsername(user.getUsername());
        userBlogResponse.setBlogs(blogResponseList);
        logger.info("Getting all user blogs which are belongs to user id '{}'", user.getId());
        return userBlogResponse;

    }


}
