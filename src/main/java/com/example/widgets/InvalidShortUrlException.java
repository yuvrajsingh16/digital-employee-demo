package com.example.widgets;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidShortUrlException extends RuntimeException {

    public InvalidShortUrlException(String message) {
        super(message);
    }

    public InvalidShortUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
