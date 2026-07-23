package com.savbill.salescrmsbss.utils;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class APIConstants {

    public static Integer SUCCESS = HttpStatus.OK.value();
    public static Integer FAIL = HttpStatus.BAD_REQUEST.value();
    public static Integer INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static Integer EXPACTIATION_FAILED = HttpStatus.EXPECTATION_FAILED.value();
    public static Integer FORBIDDEN = HttpStatus.FORBIDDEN.value();
    public static Integer NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final String ERROR_TAG = "ERROR";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();
    public static final String PASS_FAIL = "PASSWORD IS FAIL";
    public static final String MESSAGE="msg";
    public static final String BATCH_PAYMENT_NOT_ASSIGNED="Not Assigned";
    public static final String BATCH_PAYMENT_ASSIGNED="Assigned";
    public static final String BATCH_PAYMENT_REASSIGNED="ReAssigned";

    //OTPConstants
    public static List<String> OTP_GENERATION_TYPE = Arrays.asList( "ALWAYS_NEW", "REUSE");

    public static final String MVNO_DELETE_UPDATE_ERROR_MSG = "Permission Denied. Unable to update/delete this record";

}
