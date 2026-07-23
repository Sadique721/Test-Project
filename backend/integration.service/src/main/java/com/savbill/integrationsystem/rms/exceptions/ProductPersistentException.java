package com.savbill.integrationsystem.rms.exceptions;

public class ProductPersistentException extends RuntimeException{
    public ProductPersistentException(String errorMessage){
        super(errorMessage);
    };
}
