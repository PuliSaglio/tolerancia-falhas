package com.tolerancia.IMD_Travel.exception;

public class FlightServiceInternalException extends RuntimeException{
    public FlightServiceInternalException(String message) {
        super(message);
    }

    public FlightServiceInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
