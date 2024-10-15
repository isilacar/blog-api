package com.scalefocus.blog_api.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(
        description = "Video Model Information"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "videos")
public class Video {

    @Schema(
            description = "Video Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            description = "Blog description which has the video "
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blogId")
    private Blog blog;

    @Schema(
            description = "Video name"
    )
    private String name;

    @Schema(
            description = "Video type"
    )
    private String type;

    @Schema(
            description = "The path where the video stored in a file"
    )
    private String filePath;
}
