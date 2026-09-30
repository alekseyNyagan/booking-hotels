package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.config.SecurityConfig;
import com.aleksey.booking.hotels.converter.JwtConverter;
import com.aleksey.booking.hotels.jwt.JwtAccessDeniedHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import(SecurityConfig.class)
public abstract class BaseWebMvcTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected CacheManager cacheManager;

    @MockitoBean
    protected JwtConverter jwtConverter;

    @MockitoBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @BeforeEach
    void setupAccessDeniedHandler() throws Exception {
        doAnswer((InvocationOnMock inv) -> {
            HttpServletResponse response = inv.getArgument(1);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }).when(jwtAccessDeniedHandler).handle(any(), any(), any());
    }
}
