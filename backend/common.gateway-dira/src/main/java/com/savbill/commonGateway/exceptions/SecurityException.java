package com.savbill.commonGateway.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityException {

    private int statusCode;
    private String message;
    public SecurityException(String message)
    {
        super();
        this.message = message;
    }
}
