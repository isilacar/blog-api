package com.scalefocus.blog_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(
        description = "Image Model Information"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "images")
public class Image {

    @Schema(
            description = "Image Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            description = "Blog Id which is related with image"
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Schema(
            description = "Image Name"
    )
    private String name;

    @Schema(
            description = "Image Type"
    )
    private String type;

    @Schema(
            description = "Image file path where image stores"
    )
    private String filePath;

}