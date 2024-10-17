package com.scalefocus.blogservice.controller;


import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import com.scalefocus.blogservice.request.BlogUpdateRequest;
import com.scalefocus.blogservice.request.TagAddRequest;
import com.scalefocus.blogservice.response.SimplifiedBlogResponse;
import com.scalefocus.blogservice.response.SimplifiedBlogResponsePagination;
import com.scalefocus.blogservice.response.UserBlogResponse;
import com.scalefocus.blogservice.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "CRUD REST APIs for Blog Resource",
        description = "CRUD REST APIs - Create Blog, Update Blog, Get Blog, Get All Blogs, Delete Blog, Add Tag to Blog,"
)
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private static final Logger logger = LogManager.getLogger(BlogController.class);

    private final BlogService blogService;

    //authenticated users can create blogs
    @Operation(
            summary = "Create Blog REST API",
            description = "Create Blog REST API is used to save blog in a database"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status 201 CREATED"
    )
    //Users can add blog with tagNames
    @PostMapping
    public ResponseEntity<BlogDto> createBlog(@RequestBody BlogCreationRequest blogCreationRequest) {
        logger.info("User with id '{}' is creating a new blog", blogCreationRequest.getUserId());
        return new ResponseEntity<>(blogService.createBlog(blogCreationRequest), HttpStatus.CREATED);

    }

    @Operation(
            summary = "Get All Blogs REST API",
            description = "Get All Blogs REST API is used to get  all the blogs from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //users can get all the blogs
    @GetMapping
    public ResponseEntity<List<BlogDto>> getAllBlogs(){
        logger.info("Getting all blogs");
        return new ResponseEntity<>(blogService.getAllBlogs(),HttpStatus.OK);
    }


    @Operation(
            summary = "Get User Blogs REST API",
            description = "Get User Blogs REST API is used to get the blogs for specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //authenticated users can get other users blogs
    @GetMapping("/{userId}")
    public ResponseEntity<UserBlogResponse> getUserBlogs(@PathVariable Long userId) {
        return new ResponseEntity<>(blogService.getUserBlogs(userId), HttpStatus.OK);
    }



    @Operation(
            summary = "Update Blog REST API",
            description = "Update Blog REST API is used to update a particular blog in the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //users can update specific blog
    @PutMapping("/{blogId}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable Long blogId,
                                              @RequestBody BlogUpdateRequest blogUpdateRequest){
        logger.info("Blog with id '{}' is updating", blogId);
        return new ResponseEntity<>(blogService.updateBlog(blogId, blogUpdateRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Add Tag To Blog REST API",
            description = "Add Tag To Blog REST API is used to add a new tag to particular blog in the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //users can add new tag to existing blogs
    @PutMapping("/{blogId}/tags")
    public ResponseEntity<BlogDto> addTagToBlog(@PathVariable Long blogId, @RequestBody TagAddRequest tagAddRequest){
        logger.info("Adding new tag to blog with id '{}'", blogId);
        return new ResponseEntity<>(blogService.addTag(blogId, tagAddRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Tag From Blog REST API",
            description = "Delete Tag From Blog REST API is used to delete a particular tag from the existing blog"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //users can remove any tag from specific blog
    @DeleteMapping("/{blogId}/tags/{tagId}")
    public ResponseEntity<BlogDto> deleteTagFromBlog(@PathVariable Long blogId, @PathVariable Long tagId){
        logger.info("Deleting tag with id '{}' from blog with id '{}'",tagId, blogId);
        return new ResponseEntity<>(blogService.removeTag(blogId, tagId), HttpStatus.OK);
    }

    @Operation(
            summary = "Get All Blogs By Tag Name REST API",
            description = "Get All Blogs By Tag Name REST API is used to get all blogs with specific tag name from the database"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //users can get all blogs by specific tagName
    @GetMapping("/tagName/{tagName}")
    public ResponseEntity<List<BlogDto>> getAllBlogsByTagName(@PathVariable String tagName){
        logger.info("Getting all blogs by tag name '{}'", tagName);
        return new ResponseEntity<>(blogService.getBlogsByTagName(tagName),HttpStatus.OK);
    }

    /**
     *
     * @param pageNumber refers to which page you want to view
     * @param pageSize refers to the number of data that will appear on the page
     * @return SimplifiedBlogResponsePagination object which includes the data with blog title and text,
     *          total number blogs, total count of the pages, current page number and number of values
     *          in a page
     */
    //users can get simplified blog list
    @GetMapping("/simplified")
    public ResponseEntity<SimplifiedBlogResponsePagination> getSimplifiedBlogs(@RequestParam int pageNumber, @RequestParam int pageSize){
        logger.info("Getting simplified blogs with pagination");
        return new ResponseEntity<>(blogService.getSimplifiedBlogs(pageNumber,pageSize),HttpStatus.OK);
    }

    @Operation(
            summary = "Delete User Blog By Username REST API",
            description = "Delete User Blog By Username Rest API is used to delete specific blog with the help of the username"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    //authenticated users can remove own blogs
    @DeleteMapping("/{blogId}/{userId}")
    public ResponseEntity<Void> deleteUserBlog(@PathVariable Long blogId, @PathVariable Long userId) {
        logger.info("Deleting blog with id '{}'",blogId);
        blogService.deleteUserBlog(blogId, userId);
        logger.info("Blog with id '{}' deleted", blogId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
            summary = "Get Blogs By Keyword REST API",
            description = "Get Blogs By Keyword Rest API is used to get specific blogs according to text,title,tags which are matching with keyword"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status 200 SUCCESS"
    )
    @GetMapping("/keywordSearching")
    public ResponseEntity<List<ElasticBlogDocument>> getBlogsByKeyword(@RequestParam String keyword) {
        return new ResponseEntity<>(blogService.searchByKeyword(keyword)
                , HttpStatus.OK);
    }

}


