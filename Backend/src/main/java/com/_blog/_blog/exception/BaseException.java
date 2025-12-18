package com._blog._blog.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for all custom domain exceptions.
 */
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
