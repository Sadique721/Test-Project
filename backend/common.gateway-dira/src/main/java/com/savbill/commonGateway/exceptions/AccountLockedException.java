package com.savbill.commonGateway.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AccountLockedException extends Throwable {
    private final String message;

    public AccountLockedException(String message) {
        this.message = message;
    }
}
