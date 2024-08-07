package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.config.JwtProperties;
import com.aleksey.booking.hotels.exception.RefreshTokenException;
import com.aleksey.booking.hotels.model.RefreshToken;
import com.aleksey.booking.hotels.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProperties jwtProperties;

    public Optional<RefreshToken> findByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        var refreshToken = RefreshToken.builder()
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration().toMillis()))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken checkRefreshToken(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Repeat sign in action!");
        }

        return token;
    }

    public RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId).orElseThrow(() ->
                new RefreshTokenException("Refresh token not found. Repeat sign in action!"));
    }

    public void deleteByToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}