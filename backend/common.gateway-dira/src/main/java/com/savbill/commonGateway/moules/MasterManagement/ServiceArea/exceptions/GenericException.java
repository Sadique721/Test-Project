package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GenericException extends Throwable {
    private final String message;

    public GenericException(String message) {
        this.message = message;
    }
}
