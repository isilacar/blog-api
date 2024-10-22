package com.scalefocus.blogservice.repository;

import com.scalefocus.blogservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}