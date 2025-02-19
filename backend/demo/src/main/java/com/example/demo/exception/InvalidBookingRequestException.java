 package com.example.demo.exception;



public class InvalidBookingRequestException extends RuntimeException {
    public InvalidBookingRequestException(String message) {
        super(message);
    }
}
