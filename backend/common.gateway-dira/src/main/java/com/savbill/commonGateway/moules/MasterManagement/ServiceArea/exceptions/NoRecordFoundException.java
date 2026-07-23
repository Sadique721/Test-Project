package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class NoRecordFoundException extends Throwable {
    private final String message;

    public NoRecordFoundException(String message) {
        this.message = message;
    }
}
