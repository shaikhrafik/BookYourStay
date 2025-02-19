package com.example.demo.exception;

public class RoleAlreadyExistException extends RuntimeException {
    public RoleAlreadyExistException(String message) {
        super(message);
    }
}
