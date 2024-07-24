package com.aleksey.booking.hotels.api.request;

import java.io.Serializable;

public record LoginRequest(String username, String password) implements Serializable {
}