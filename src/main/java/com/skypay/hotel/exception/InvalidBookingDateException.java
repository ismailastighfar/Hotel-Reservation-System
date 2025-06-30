package com.skypay.hotel.exception;

public class InvalidBookingDateException extends RuntimeException {
    public InvalidBookingDateException(String message) {
        super(message);
    }

    public InvalidBookingDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
