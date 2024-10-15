package com.scalefocus.blog_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Schema(
        description = "Blog Entity Model Information"
)
@Entity
@Table(name = "BLOG")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Blog {

    @Schema(
            description = "Blog Entity Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            description = "Blog Entity Title"
    )
    private String title;

    @Schema(
            description = "Blog Entity Text"
    )
    private String text;

    @Schema(
            description = "Blog Entity Tag Information"
    )
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(name = "blog_tags",
            joinColumns ={ @JoinColumn(name = "blog_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")}
    )
    private Set<Tag> tags = new HashSet<>();

    @Schema(
            description = "Blog belong to which user information Information"
    )
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
