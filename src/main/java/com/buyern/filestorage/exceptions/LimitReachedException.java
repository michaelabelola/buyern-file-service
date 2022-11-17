package com.buyern.filestorage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class LimitReachedException extends RuntimeException {

    public LimitReachedException(String message) {
        super(message);
    }
}
