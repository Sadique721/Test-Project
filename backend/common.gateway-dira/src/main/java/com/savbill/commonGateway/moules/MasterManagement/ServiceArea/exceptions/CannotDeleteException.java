package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CannotDeleteException extends Throwable {

    private final String message;

    public CannotDeleteException(String message) {
        this.message = message;
    }
}
