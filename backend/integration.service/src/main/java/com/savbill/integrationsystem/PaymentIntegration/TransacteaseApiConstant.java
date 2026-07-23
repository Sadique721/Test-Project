package com.savbill.integrationsystem.PaymentIntegration;
import java.util.HashMap;
import java.util.Map;

public class TransacteaseApiConstant {
    public static final Map<String, String> STATUS_ABBREVIATIONS = new HashMap<>();


    public interface STATUS_CODE {
        String SUCCESS = "000";
        String FAILED = "001";
        String CANCELED = "002";
        String DATA_ALREADY_EXISTS = "003";
        String SESSION_TIMEOUT = "005";
        String INVALID_FIELD_INFO = "012";
        String INVALID_FORMAT = "013";
        String SYSTEM_ERROR = "014";
        String INVALID_HASH = "016";
        String DATA_NOT_FOUND = "017";
        String ENCRYPTION_ERROR = "018";
        String INVALID_AUTHORIZATION = "998";
        String INVALID_AUTHENTICATION = "999";
    }


    static {
        // Response Code Descriptions
        STATUS_ABBREVIATIONS.put(STATUS_CODE.SUCCESS, "Success");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.FAILED, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.CANCELED, "Canceled");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.DATA_ALREADY_EXISTS, "DAE");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.SESSION_TIMEOUT, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.INVALID_FIELD_INFO, "IFI");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.INVALID_FORMAT, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.SYSTEM_ERROR, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.INVALID_HASH, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.DATA_NOT_FOUND, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.ENCRYPTION_ERROR, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.INVALID_AUTHORIZATION, "Failed");
        STATUS_ABBREVIATIONS.put(STATUS_CODE.INVALID_AUTHENTICATION, "Failed");
    }

    public static String getStatusAbbreviation(String code) {
        return STATUS_ABBREVIATIONS.getOrDefault(code, "Failed");
    }
}
