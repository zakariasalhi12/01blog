package com._blog._blog.exception;

import org.springframework.http.HttpStatus;

public class NotificationException extends BaseException {

    public NotificationException(String message, HttpStatus status) {
        super(message, status);
    }
}
