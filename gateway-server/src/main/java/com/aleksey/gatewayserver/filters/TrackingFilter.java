package com.aleksey.gatewayserver.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
@Slf4j
@RequiredArgsConstructor
public class TrackingFilter implements GlobalFilter {

    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (isCorrelationIdPresent(headers)) {
            log.debug("tmx-correlation-id found in tracking filter {}.", filterUtils.getCorrelationId(headers));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationId);
            log.debug("tmx-correlation-id generated in tracking filter {}.", correlationId);
        }

        System.out.println("The authentication name from the token is : " + getUsername(headers));

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders headers) {
        return filterUtils.getCorrelationId(headers) != null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    private String getUsername(HttpHeaders requestHeaders) {
        String username = "";
        if (filterUtils.getAuthToken(requestHeaders) != null) {
            String authToken = filterUtils.getAuthToken(requestHeaders).replace("Bearer ", "");
            JSONObject jsonObj = decodeJWT(authToken);
            try {
                username = jsonObj.getString("preferred_username");
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }
        return username;
    }

    private JSONObject decodeJWT(String JWTToken) {
        String[] split_string = JWTToken.split("\\.");
        String base64EncodedBody = split_string[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));
        return new JSONObject(body);
    }
}