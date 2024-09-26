package com.aleksey.statisticservice.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private static final String KEYCLOAK_RESOURCE_ACCESS = "resource_access";
    private static final String RESOURCE_ID = "booking-hotels";
    private static final String KEYCLOAK_ROLES = "roles";
    private static final String KEYCLOAK_ROLE_PREFIX = "ROLE_";
    private static final String PRINCIPAL_ATTRIBUTE = "preferred_username";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(@NonNull Jwt jwt) {
        Set<SimpleGrantedAuthority> accesses = Optional.of(jwt)
                .map(token -> token.getClaimAsMap(KEYCLOAK_RESOURCE_ACCESS))
                .map(claimMap -> (Map<String, Object>) claimMap.get(RESOURCE_ID))
                .map(resourceData -> (Collection<String>) resourceData.get(KEYCLOAK_ROLES))
                .stream()
                .flatMap(Collection::stream)
                .map(role -> new SimpleGrantedAuthority(KEYCLOAK_ROLE_PREFIX + role))
                .collect(Collectors.toSet());

        Set<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(), accesses.stream()
        ).collect(Collectors.toSet());

        String principalClaimName = Optional.ofNullable(jwt.getClaimAsString(PRINCIPAL_ATTRIBUTE))
                .orElse(jwt.getClaimAsString(JwtClaimNames.SUB));

        return new JwtAuthenticationToken(jwt, authorities, principalClaimName);
    }
}