package com.scalefocus.blog_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(
        description = "Token Model Information"
)
@Entity
@Table(name = "TOKEN")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    @Schema(
            description = "Token Id"
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Schema(
            description = "Token Value"
    )
    private String token;
    @Schema(
            description = "Token Expiration Time"
    )
    private boolean isExpired;

    @Schema(
            description = "User Token Information"
    )
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}