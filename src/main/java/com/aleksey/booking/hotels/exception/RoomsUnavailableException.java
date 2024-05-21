package com.aleksey.booking.hotels.exception;

public class RoomsUnavailableException extends RuntimeException {
    public RoomsUnavailableException(String message) {
        super(message);
    }
}