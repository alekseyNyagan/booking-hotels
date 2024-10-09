package com.aleksey.gatewayserver.filters;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResponseFilter {

    public static final String CORRELATION_ID = "tmx-correlation-id";

    private final Tracer tracer;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            String traceId = Objects.isNull(tracer.currentSpan()) ? tracer.nextSpan().context().traceIdString() :
                    tracer.currentSpan().context().traceIdString();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.debug("Adding the correlation id to the outbound headers. {}", traceId);
                exchange.getResponse().getHeaders().add(CORRELATION_ID, traceId);
                log.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());

            }));
        };
    }
}