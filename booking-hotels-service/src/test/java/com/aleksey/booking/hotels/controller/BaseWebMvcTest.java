package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.config.SecurityConfig;
import com.aleksey.booking.hotels.converter.JwtConverter;
import com.aleksey.booking.hotels.jwt.JwtAccessDeniedHandler;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(SecurityConfig.class)
public abstract class BaseWebMvcTest {

    @MockitoBean
    protected CacheManager cacheManager;

    @MockitoBean
    protected JwtConverter jwtConverter;

    @MockitoBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;
}
