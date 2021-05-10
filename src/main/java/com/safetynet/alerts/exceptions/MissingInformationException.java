package com.safetynet.alerts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingInformationException extends RuntimeException {
    public MissingInformationException(String message) {
        super(message);
    }
}
