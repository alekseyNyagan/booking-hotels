package com.aleksey.booking.hotels.security;

import com.aleksey.booking.hotels.api.request.LoginRequest;
import com.aleksey.booking.hotels.api.request.RefreshTokenRequest;
import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.api.response.AuthResponse;
import com.aleksey.booking.hotels.api.response.RefreshTokenResponse;
import com.aleksey.booking.hotels.api.response.UserResponse;
import com.aleksey.booking.hotels.exception.AlreadyExistsException;
import com.aleksey.booking.hotels.exception.RefreshTokenException;
import com.aleksey.booking.hotels.mapper.UserMapper;
import com.aleksey.booking.hotels.model.RefreshToken;
import com.aleksey.booking.hotels.model.User;
import com.aleksey.booking.hotels.repository.UserRepository;
import com.aleksey.booking.hotels.security.jwt.JwtUtils;
import com.aleksey.booking.hotels.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public AuthResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return AuthResponse.builder()
                .id(userDetails.getId())
                .token(jwtUtils.generateJwtToken(userDetails))
                .refreshToken(refreshToken.getToken())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public UserResponse register(UpsertUserRequest upsertUserRequest) {
        if (userRepository.existsByName(upsertUserRequest.name())) {
            throw new AlreadyExistsException(MessageFormat.format("Пользователь c именем {0} уже существует!", upsertUserRequest.name()));
        }

        if (userRepository.existsByEmail(upsertUserRequest.email())) {
            throw new AlreadyExistsException(MessageFormat.format("Пользователь c email {0} уже существует!", upsertUserRequest.email()));
        }

        User user = userRepository.save(userMapper.toEntity(upsertUserRequest));

        return userMapper.toDto(user);
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        return refreshTokenService.findByRefreshToken(requestRefreshToken)
                .map(refreshTokenService::checkRefreshToken)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    User tokenOwner = userRepository.findById(userId).orElseThrow(() ->
                            new RefreshTokenException(MessageFormat.format("Exception: trying to get token for userId {0}", userId)));
                    String token = jwtUtils.generateTokenFromUsername(tokenOwner.getName());

                    return new RefreshTokenResponse(token, requestRefreshToken);
                }).orElseThrow(() -> new RefreshTokenException("Refresh token not found"));
    }

    public void logout() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentPrincipal instanceof AppUserDetails userDetails) {
            Long userId = userDetails.getId();
            RefreshToken refreshToken = refreshTokenService.findByUserId(userId);
            refreshTokenService.deleteByToken(refreshToken);
        }
    }
}