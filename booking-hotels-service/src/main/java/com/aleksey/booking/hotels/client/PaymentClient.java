package com.aleksey.booking.hotels.client;

import com.aleksey.booking.hotels.api.request.PaymentRequest;
import com.aleksey.booking.hotels.api.response.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://payment-service")
                .build();
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackProcessPayment")
    @Retry(name = "paymentService")
    public PaymentResponse processPayment(Long bookingId, UUID userId, BigDecimal totalCost) {
        String jwtToken = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication())
                .getToken().getTokenValue();

        log.info("Sending payment request for bookingId={} via RestClient", bookingId);

        return restClient.post()
                .uri("/api/payment/process")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body(new PaymentRequest(bookingId, userId, totalCost))
                .retrieve()
                .body(PaymentResponse.class);
    }

    public PaymentResponse fallbackProcessPayment(Long bookingId, UUID userId, BigDecimal totalCost, Throwable throwable) {
        log.error("💥 Fallback activated for bookingId={}. Reason: {}", bookingId, throwable.getMessage());
        return new PaymentResponse(null, "PENDING_PAYMENT_FALLBACK");
    }
}
