package com.aleksey.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.*;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
            ServerLogoutSuccessHandler logoutSuccessHandler,
            ServerLogoutHandler logoutHandler) {
        return http
                .authorizeExchange(
                        exchange ->
                                exchange.pathMatchers(
                                                "/actuator/**",
                                                "/access-token/**",
                                                "id-token")
                                        .permitAll()
                                        .anyExchange()
                                        .authenticated()
                ).oauth2Login(oAuth2LoginSpec ->
                        oAuth2LoginSpec.authorizationRequestResolver(authorizationRequestResolver)
                                .authorizedClientRepository(authorizedClientRepository)
                )
                .logout(logoutSpec ->
                        logoutSpec.logoutSuccessHandler(logoutSuccessHandler)
                                .logoutHandler(logoutHandler)
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    ServerOAuth2AuthorizationRequestResolver requestResolver(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    ServerLogoutSuccessHandler logoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    ServerLogoutHandler logoutHandler() {
        return new DelegatingServerLogoutHandler(
                new SecurityContextServerLogoutHandler(),
                new WebSessionServerLogoutHandler(),
                new HeaderWriterServerLogoutHandler(
                        new ClearSiteDataServerHttpHeadersWriter(ClearSiteDataServerHttpHeadersWriter.Directive.COOKIES)
                )
        );
    }
}
