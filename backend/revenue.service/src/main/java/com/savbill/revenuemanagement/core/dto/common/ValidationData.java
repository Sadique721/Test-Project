package com.savbill.revenuemanagement.core.dto.common;

import lombok.Data;

@Data
public class ValidationData {
    boolean isValid = true;
    String message;

    public ValidationData() {
    }

    public ValidationData(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }
}
