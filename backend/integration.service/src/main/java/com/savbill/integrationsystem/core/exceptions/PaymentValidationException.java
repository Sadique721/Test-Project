package com.savbill.integrationsystem.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaymentValidationException extends RuntimeException {

    private String resultCode;
    private int httpStatus;

    public PaymentValidationException(String message, String resultCode, int httpStatus) {
        super(message);
        this.resultCode = resultCode;
        this.httpStatus = httpStatus;
    }
    public PaymentValidationException(String message, String resultCode){
        super(message);
        this.resultCode = resultCode;

    }
}
