package com.savbill.integrationsystem.core.utillity;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class APIConstants {

    public static Integer SUCCESS = HttpStatus.OK.value();
    public static Integer FAIL = HttpStatus.BAD_REQUEST.value();
    public static Integer INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static Integer FORBIDDEN = HttpStatus.FORBIDDEN.value();
    public static Integer NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final String ERROR_TAG = "ERROR";
    public static final Integer EXPECTATION_FAILED=HttpStatus.EXPECTATION_FAILED.value();
    public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();
    public static final String SELFCARE = "SelfCare";
    public static final String PAYTYPE = "Payment";
    public static final String NA = "NA";
    public static final String COMPLETED = "Completed";
    public static final String FULLY_ADJUSTED = "Fully Adjusted";
    public static final String PARTIALY_ADJUSTED = "Partialy Adjusted";
    public static final String APPROVED = "approved";
    public static final String VERIFIED = "Verified";
    public static Integer NO_CONTENT_FOUND = HttpStatus.NO_CONTENT.value();
    public static final String ACTIVE_STATUS = "Active";

    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String MESSAGE = "msg";

    public static final String PROFILE_NAME="NetSuite";

    public static final String TRA_INVOICE = "TraInvoice";

    public static final String AIRTEL = "Airtel";

    public static final String MOMO_PAY = "MoMo Pay";
    public static final String PAYSTACK_PAY = "PayStack Pay";
    public static final String MPESA_PAY = "Mpesa Pay";

    public static final String ONE_PAY = "One Pay";

    public static final String TRADELANCE = "Tradelance";


    public static final String TYPE = "type";

    public static final String TYPE_FETCH = "fetch";

    public static final String DOWNLOAD_ONLINEPAYADUIT = "download_onlinePayAduit";




}
