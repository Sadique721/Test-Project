package com.savbill.commonGateway.exceptions;

public class PincodeNotInServiceAreaException extends RuntimeException {
    public PincodeNotInServiceAreaException(String message) {
        super(message);
    }
}