package com.savbill.integrationsystem.AirtelAppToCRM;

public class AirtelValidateTxValidator extends RuntimeException{

    public int statusCode;
    public String reference;

    // Constructor with a custom message
    public AirtelValidateTxValidator(String message) {
        super(message);
    }

    public AirtelValidateTxValidator(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AirtelValidateTxValidator(String message, int statusCode,String reference) {
        super(message);
        this.statusCode = statusCode;
        this.reference = reference;
    }


    // Constructor with a custom message and a cause
    public AirtelValidateTxValidator(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode=statusCode;
    }

    // Constructor with a cause
    public AirtelValidateTxValidator(Throwable cause) {
        super(cause);
    }

}
