package com.scalefocus.blog_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Schema(
        description = "Tag Model Information"
)
@Entity
@Table(name = "TAG")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tag {
    @Schema(
            description = "Tag Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            description = "Tag Name"
    )
    private String name;

    @Schema(
            description = "Tags Related With Blogs"
    )
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Blog> blogs = new HashSet<>();
}
