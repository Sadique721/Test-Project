package com.savbill.commonGateway.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PasswordExpiryException extends Throwable {
    private final String message;

    public PasswordExpiryException(String message) {
        this.message = message;
    }
}
