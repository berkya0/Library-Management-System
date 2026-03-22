package com.berkaykomur.repository;

import com.berkaykomur.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_token", nativeQuery = true)
    void hardDeleteAll();

}
