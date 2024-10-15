package com.scalefocus.blog_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(
        description = "User Model Information"
)
@Entity
@Table(name = "user_tbl")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Schema(
            description = "User Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            description = "User Username"
    )
    @Column(unique = true, nullable = false)
    private String username;

    @Schema(
            description = "User Password"
    )
    @Column(nullable = false)
    private String password;

    @Schema(
            description = "User Display Name"
    )
    private String displayName;

    @Schema(
            description = "User Token List"
    )
    @OneToMany(mappedBy = "user")
    private List<Token> tokenList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Blog> blogList = new HashSet<>();
}