package com.scalefocus.userservice.repository;


import com.scalefocus.userservice.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t inner join User u on t.user.id=u.id where u.id= :userId and t.isExpired=false")
    List<Token> findAllNonExpiredTokens(Long userId);

    Optional<Token> findByToken(String token);
}