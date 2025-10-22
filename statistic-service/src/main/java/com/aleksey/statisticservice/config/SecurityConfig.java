package com.aleksey.statisticservice.config;

import com.aleksey.statisticservice.converter.JwtConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtConverter jwtAuthenticationConverter) throws Exception {
        return http
                .securityMatcher("/api/v1/statistics/**")
                .authorizeHttpRequests((auth) -> auth.anyRequest().hasAuthority("ROLE_ADMIN"))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                                .decoder(jwtDecoder())
                ))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("prometheus")
                .password("{noop}prometheus")
                .roles("ACTUATOR")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}