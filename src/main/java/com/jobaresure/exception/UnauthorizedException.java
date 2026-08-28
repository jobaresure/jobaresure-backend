package com.jobaresure.exception;

/** Thrown for failed authentication / invalid credentials / invalid tokens. */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
