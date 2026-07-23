package com.savbill.notification.utils;

//import liquibase.pro.packaged.S;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonConstants {
	private CommonConstants() {
		 throw new IllegalStateException("Utility class");
	}
	public static final Integer SUCCESS=HttpStatus.OK.value();
	public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
	public static final Integer INTERNAL_SERVER_ERROR=HttpStatus.INTERNAL_SERVER_ERROR.value();
	public static final String ERROR_TAG="ERROR";
	
	public static final String BLANK_STRING = "string";
	public static final String IN_ACTIVE = "Inactive";
	public static final String ACTIVE = "Active";
	public static final String VALIDATION_REASON = "ValidationReason";
	public static final String BASIC_STRING_MSG = "BasicStringMsg";
	public static final String BASIC_NUMERIC_MSG = "BasicNumericMsg";
	public static final String VALIDATION_REASON_BASIC_STRING_MESSAGE="Null, blank value and 'string' are not allowed";
	public static final String VALIDATION_REASON_BASIC_NUMERIC_MESSAGE="Null and 0 are not allowed";
	
	public static final String USER_ADMIN ="Admin";
	
	
	public static final String TIMESTAMP="timestamp";
	public static final String ERROR_MESSAGE="errorMessage";
	public static final String STATUS="status";
	
	public static final String MESSAGE="message";
	public static final String LOGIN_SUCCESSFULLY="Login Successfully.";
	public static final String BAD_CREDENTIALS="Bad Credentials";
	public static final String COMMON_SERVICE_DOWN="Operation Failed..Common Service is Down";
	
	public static final String DATE_PATTERN="yyyy-MM-dd HH:mm:ss:SSSS";
    public static final String RESPONSE_CODE="responseCode";
	public static final String SERVICE_TYPE_BSS = "BSS";
	public static final String SERVICE_TYPE_IWF = "IWF";
	public static final Integer UNAUTHORIZED=HttpStatus.UNAUTHORIZED.value();
    public static final Integer FORBIDDEN=HttpStatus.FORBIDDEN.value();
	public static final Integer EXPECTATION_FAILED = HttpStatus.EXPECTATION_FAILED.value();
    public static final String REQUEST_FOR = "requestFor";
	public static final String POST = "POST";
	public static  final String  CONTENT_TYPE ="H-Content-Type";
	public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public interface OPERATION {
		public static final Integer OPERATION_ADD = 1;
		public static final Integer OPERATION_UPDATE = 2;
		public static final Integer OPERATION_DELETE = 3;
	}

	public static String GetError(Exception e){
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		double msgLength =(Integer) stringWriter.toString().length()*0.15;
		int endIndx = (int) msgLength;
		return stringWriter.toString().substring(0,endIndx);
	}
	public static final String AUDIT_LOG = "audit_log";

	public interface MODULE{
		String MODULE_NOTIFICATION = "Notification Management";
	}
}
