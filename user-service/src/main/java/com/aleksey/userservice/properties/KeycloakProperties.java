package com.aleksey.userservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {

    private String serverUrl;

    private String realm;

    private String username;

    private String password;

    private String resource;

    private Credentials credentials;

    @Data
    public static class Credentials {
        private String secret;
    }
}