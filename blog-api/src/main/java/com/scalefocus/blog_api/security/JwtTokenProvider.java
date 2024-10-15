package com.scalefocus.blog_api.security;

import com.scalefocus.blog_api.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class);

    private String jwtSecretKey;

    private long jwtExpirationMilliseconds;

    public String generateToken(User user) {
        logger.info("Generating token for user with id '{}'", user.getId());
        String username = user.getUsername();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        logger.info("Getting username from token");
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        logger.info("Validating token");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return !isTokenExpired(token);
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException
                 | UnsupportedJwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT", e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        logger.info("Checking if token is expired");
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getExpiration();

        return expiration.before(new Date());
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecretKey)
        );
    }
}