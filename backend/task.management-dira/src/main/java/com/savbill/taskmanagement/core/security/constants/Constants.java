package com.savbill.taskmanagement.core.security.constants;

import org.springframework.http.HttpStatus;

public class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final Integer SUCCESS= HttpStatus.OK.value();
    public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
    public static final Integer EMPTY=HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();
    public static final Integer INACTIVE=462;
    public static final Integer EXPIRED=459;
    public static final Integer QUOTA=451;
    public static final Integer NOTFOUND=499;
    public static final Integer SUSPEND=401;
    public static final String QUOTA_USED = "quotaUsed";
    public static final Integer INTERNAL_SERVER_ERROR=HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String ERROR_TAG="ERROR";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String MESSAGE = "message";
    public static final String BLANK_STRING = "string";
    public static final String IN_ACTIVE = "Inactive";
    public static final String ACTIVE = "Active";
    public static final String SUSPENDED = "Suspended";
    public static final String VALIDATION_REASON = "ValidationReason";
    public static final String BASIC_STRING_MSG = "BasicStringMsg";
    public static final String BASIC_NUMERIC_MSG = "BasicNumericMsg";
    public static final String NOT_FOUND = "NotFound";
    public static final String EXPIRED_USER = "expired";
    public static final String VALIDATION_REASON_BASIC_STRING_MESSAGE="Null, blank value and 'string' are not allowed";
    public static final String VALIDATION_REASON_BASIC_NUMERIC_MESSAGE="Null and 0 are not allowed";
    public static final String COA="CoA";
    public static final String DM="DM";
    public static final String NOT_PUT_IN_QUEUE = "NotPutInQueue";
    public static final String USER_NAME = "userName";
    public static final String TYPE_FETCH = "fetch";
    public static final String TYPE_UPDATE = "update";
    public static final String TYPE_DELETE = "delete";
    public static final String TYPE_CREATE = "create";
    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_LOGOUT = "logout";
    public static final String TYPE = "type";
    public static final String TIME = "Time";
    public static final String VOLUME = "Volume";
    public static final String MIN = "Min";
    public static final String HOUR = "Hour";
    public static final String DAY = "Day";
    public static final String MB = "MB";
    public static final String GB = "GB";

    public static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final long FORGOT_PWD_EXPIRATION_TIME = 900;

    public static final Integer DEFAULT_PARTNER_ID = 1;
}
