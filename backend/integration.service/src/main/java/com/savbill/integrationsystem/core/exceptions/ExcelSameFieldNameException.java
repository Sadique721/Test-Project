package com.savbill.integrationsystem.core.exceptions;

public class ExcelSameFieldNameException extends RuntimeException {
    public ExcelSameFieldNameException() {
        super("Excel field name must be unique.");
    }

    public ExcelSameFieldNameException(String message) {
        super(message);
    }
}
