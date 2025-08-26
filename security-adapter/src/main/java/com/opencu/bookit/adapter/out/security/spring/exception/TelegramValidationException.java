package com.opencu.bookit.adapter.out.security.spring.exception;

import org.springframework.security.core.AuthenticationException;

public class TelegramValidationException extends AuthenticationException {
    public TelegramValidationException(String msg) {
        super(msg);
    }
}