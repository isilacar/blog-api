package com.scalefocus.blogservice.repository;


import com.scalefocus.blogservice.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
