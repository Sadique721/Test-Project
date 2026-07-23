package com.savbill.radius.utils;

import org.springframework.http.HttpStatus;

public class RadiusConstants {
	private RadiusConstants() {
		 throw new IllegalStateException("Utility class");
	}
	public static final Integer SUCCESS=HttpStatus.OK.value();
	public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
	public static final Integer EXPECTATION_FAILED =HttpStatus.EXPECTATION_FAILED.value();
	public static final Integer EMPTY=HttpStatus.INTERNAL_SERVER_ERROR.value();
	public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();

	public static final Integer  NO_CONTENT_FOUND =HttpStatus.NO_CONTENT.value();
	public static final Integer INACTIVE=462;
	public static final Integer EXPIRED=459;
	public static final Integer QUOTA=451;
	public static final Integer NOTFOUND=499;
	public static final String QUOTA_USED = "quotaUsed";
	public static final Integer INTERNAL_SERVER_ERROR=HttpStatus.INTERNAL_SERVER_ERROR.value();
	public static final String ERROR_TAG="ERROR";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String MESSAGE = "message";
	public static final String BLANK_STRING = " ";
	public static final String IN_ACTIVE = "Inactive";
	public static final String ACTIVE = "Active";
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
	public static final String TYPE_AUTH = "authentication";
	public static final String TYPE_ACCT = "accounting";
	public static final String TYPE_DELETE = "delete";
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_LOGIN = "login";
	public static final String TYPE_LOGOUT = "logout";
	public static final String TYPE = "type";
	public static final String TRACE_ID = "traceId";
	public static final String SPAN_ID = "spanId";
	public static final String USERNAME = "userName";
	public static final String CALLEDSTATIONID = "Called-Station-Id";
	public static final String CALLINGSTATIONID = "Calling-Station-Id";

	public static final String TIME = "Time";
	public static final String VOLUME = "Volume";
	public static final String MIN = "Min";
	public static final String HOUR = "Hour";
	public static final String DAY = "Day";
	public static final String MB = "MB";
	public static final String GB = "GB";


	public static String PDF_PATH = "pdfpath";
	public static final String ALLOWED_DOCUMENT_SIZE = "allowedDocumentSize";

	public static final String RESERVED = "Reserved";
	public static final String FREE = "Free";
	public static final String ALLOCATED = "Allocated";

	public static final String START = "Start";

	public static final String STOP = "Stop";

	public static final String INTERIM_UPDATE = "Interim-Update";

	public static String BULK_VLAN_PATH = "bulkvlanpath";

}
