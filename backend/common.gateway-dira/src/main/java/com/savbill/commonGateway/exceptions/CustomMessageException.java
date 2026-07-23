package com.savbill.commonGateway.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CustomMessageException extends Throwable {
    private final String message;

    public CustomMessageException(String message) {
        this.message = message;
    }
}
