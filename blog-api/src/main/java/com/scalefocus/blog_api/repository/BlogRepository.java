package com.scalefocus.blog_api.repository;


import com.scalefocus.blog_api.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findByTagsName(String name);

    List<Blog> getBlogsByUserUsername(String name);
}
