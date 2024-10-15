package com.scalefocus.blog_api.repository;

import com.scalefocus.blog_api.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
