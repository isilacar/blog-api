package com.scalefocus.blogservice.repository;

import com.scalefocus.blogservice.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
