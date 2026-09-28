package com.renteasy.exception;

public class RoomAlreadyOccupiedException extends RuntimeException {
    public RoomAlreadyOccupiedException(String message) {
        super(message);
    }
}
