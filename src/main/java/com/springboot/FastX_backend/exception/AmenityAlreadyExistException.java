package com.springboot.FastX_backend.exception;

public class AmenityAlreadyExistException extends RuntimeException{
    public AmenityAlreadyExistException(String message) {
        super(message);
    }
}
