package com.savbill.notification.utils;


public class LogConstants {

    public static final String REQUEST_FROM = "Request from : ";

    public static final String REQUEST_FOR = " , Request for : ";

    public static final String REQUEST_TO_CREATE = " , Request to create";

    public static final String REQUEST_TO_FETCH = " , Request to fetch";

    public static final String REQUEST_TO_UPDATE = " , Request to update";

    public static final String REQUEST_TO_DELETE = " , Request to delete";

    public static final String REQUEST_BY = " , Request by : ";

    public static final String LOG_STATUS = " , STATUS : ";
    public static final String LOG_STATUS_CODE = " , STATUS_CODE  ";

    public static final String LOG_SUCCESS = "SUCCESS ";

    public static final String LOG_FAILED = "FAILED ";

    public static final String LOG_NOT_FOUND = "NOT FOUND ";

    public static final String LOG_ERROR = " ERROR : ";
    public static final String ERROR_MESSAGE = " ERROR : ";

    public static final String LOG_INFO = "INFO : ";

    public static final String LOG_UNAUTHORIZED = "UNAUTHORIZED ";

    public static final String LOG_NOT_CREATED = "NOT CREATED";
    public static final String LOG_NO_RECORD_FOUND = "No records found !!";

    public static final String LOG_BY_NAME = " Entity Name : ";

    public interface LogConstant {
        public final String LOG_MESSAGE = "{{\"requestfrom\":\"{}\", \"requestby\":\"{}\", \"requestip\":\"{}\", \"type\":\"{}\", \"status\":\"{}\", \"statuscode\":\"{}\", \"message\":\"{}\"}}";
        public final String CREATE_TYPE = "CREATE";
        public final String UPDATE_TYPE = "UPDATE";
        public final String DELETE_TYPE = "DELETE";
        public final String FETCH_TYPE = "FETCH";
        public final String SUCCESS_STATUS = "SUCCESS";
        public final String FAIL_STATUS = "FAILURE";
        public final String REQUEST_FROM = "requestFrom";
        public final String AUTHORIZATION = "Authorization";
        public final String TRACE_ID = "traceId";
        public final String SPAN_ID = "spanId";
    }
}
