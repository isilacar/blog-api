package com.scalefocus.blog_api.repository;

import com.scalefocus.blog_api.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}