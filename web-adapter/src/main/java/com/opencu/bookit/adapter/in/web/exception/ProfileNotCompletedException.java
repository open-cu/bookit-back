package com.opencu.bookit.adapter.in.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProfileNotCompletedException extends RuntimeException {
    public ProfileNotCompletedException(String message) {
        super(message);
    }
}