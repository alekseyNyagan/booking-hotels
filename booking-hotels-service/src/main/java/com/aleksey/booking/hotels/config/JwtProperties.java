package com.aleksey.booking.hotels.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtProperties {
    private String secret;

    private Duration tokenExpiration;

    private Duration refreshTokenExpiration;
}