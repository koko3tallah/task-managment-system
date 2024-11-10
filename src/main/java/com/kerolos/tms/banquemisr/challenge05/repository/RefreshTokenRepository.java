package com.kerolos.tms.banquemisr.challenge05.repository;

import com.kerolos.tms.banquemisr.challenge05.data.entity.RefreshToken;
import com.kerolos.tms.banquemisr.challenge05.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);
}
