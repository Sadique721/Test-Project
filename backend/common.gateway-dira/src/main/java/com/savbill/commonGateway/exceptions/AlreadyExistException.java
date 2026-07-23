package com.savbill.commonGateway.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AlreadyExistException extends Throwable {
    private final String message;

    public AlreadyExistException(String message) {
        this.message = message;
    }
}
