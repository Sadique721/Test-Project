package com.savbill.notification.utils;

public class MessageConstants 
{
	public static final String INVALID_MVNOID_MESSAGE="Bad Request..Please Enter Valid MvnoId";
	public static final String GENRAL_AUTHORIZATION_FAILURE="You are not authorized to perform this operation";
    public static final String INAVLID_DATA_MESSAGE="No such record exists for :- ";
    public static final String DATABASE_EXCEPTION_MESSAGE="Database Connection Failure";
    public static final String TOKEN_NOT_PRESENT_MESSAGE="Authentication Failed..Could not find Token in request headers";
    public static final String BEARER_STRING_NOT_PRESENT_MESSAGE="Authentication Failed..Could not find Bearer String";
    public static final String INVALID_TOKEN_MESSAGE="Authentication Failed..Token is invalid";
    public static final String NO_SUCH_USERNAME_PRESENT="Authentication Failed..username from token does not exist";
    public static final String MVNO_MISMATCH_MESSAGE="Mvno id does not match with the given record";
    public static final String AUTHORIZATION_HEADER_NOT_PRESENT_MESSAGE="Authenitcation Failed..Authorization Header not present";

    public static final String TOKEN_EXPIRY_MESSAGE="Authentication Failed..Please re-login..Your token has expired";
    public static final String  TOKEN_VALIDATION_FAILURE_MESSAGE="Failed to validate token..Database Connection Exception";
 
}
