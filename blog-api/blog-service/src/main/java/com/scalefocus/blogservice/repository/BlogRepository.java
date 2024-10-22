package com.scalefocus.blogservice.repository;

import com.scalefocus.blogservice.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findByTagsName(String name);

    List<Blog> getBlogsByUserId(Long userId);
}
