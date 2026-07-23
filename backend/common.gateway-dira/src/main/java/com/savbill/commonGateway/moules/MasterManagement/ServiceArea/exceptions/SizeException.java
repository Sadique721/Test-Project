package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class SizeException extends Throwable {
    private final String message;

    public SizeException(String message) {
        this.message = message;
    }
}
