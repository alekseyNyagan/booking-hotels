package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.LoginRequest;
import com.aleksey.booking.hotels.api.request.RefreshTokenRequest;
import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.api.response.AuthResponse;
import com.aleksey.booking.hotels.api.response.RefreshTokenResponse;
import com.aleksey.booking.hotels.api.response.SimpleResponse;
import com.aleksey.booking.hotels.api.response.UserResponse;
import com.aleksey.booking.hotels.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SecurityService securityService;

    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticate(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UpsertUserRequest upsertUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securityService.register(upsertUserRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(securityService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("logout")
    public ResponseEntity<SimpleResponse> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();
        return ResponseEntity.ok(new SimpleResponse(MessageFormat.format("User logout. Username is: {0}", userDetails.getUsername())));
    }
}