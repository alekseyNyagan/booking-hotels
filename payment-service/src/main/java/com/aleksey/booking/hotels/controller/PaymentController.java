package com.aleksey.booking.hotels.controller;

import com.aleksey.booking.hotels.api.request.PaymentRequest;
import com.aleksey.booking.hotels.api.request.response.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) throws InterruptedException {
        double chance = Math.random();

        if (chance < 0.25) {
            Thread.sleep(4000);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        } else if (chance < 0.50) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(new PaymentResponse(UUID.randomUUID(), "SUCCESS"));
    }
}
