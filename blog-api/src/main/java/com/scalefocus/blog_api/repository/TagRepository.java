package com.scalefocus.blog_api.repository;

import com.scalefocus.blog_api.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
}
