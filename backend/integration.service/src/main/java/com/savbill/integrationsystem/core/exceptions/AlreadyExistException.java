package com.savbill.integrationsystem.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AlreadyExistException extends RuntimeException {
    private final String message;

    public AlreadyExistException(String message) {
        this.message = message;
    }
}
