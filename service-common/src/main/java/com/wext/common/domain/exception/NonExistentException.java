package com.wext.common.domain.exception;

public class NonExistentException extends RuntimeException {
    public NonExistentException(String message) {
        super(message);
    }
}
