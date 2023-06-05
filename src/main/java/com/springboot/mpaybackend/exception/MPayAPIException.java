package com.springboot.mpaybackend.exception;

import org.springframework.http.HttpStatus;

public class MPayAPIException extends RuntimeException {

    private HttpStatus status;
    private String message;

    public MPayAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public MPayAPIException(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
