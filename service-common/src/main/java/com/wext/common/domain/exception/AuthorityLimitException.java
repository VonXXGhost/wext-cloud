package com.wext.common.domain.exception;

public class AuthorityLimitException extends RuntimeException {
    public AuthorityLimitException(String message) {
        super(message);
    }
}
