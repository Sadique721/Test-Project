package com.savbill.inventorymanagement.core.constants;

import org.springframework.http.HttpStatus;

public class Constants {
	private Constants() {
		 throw new IllegalStateException("Utility class");
	}
	public static final Integer SUCCESS=HttpStatus.OK.value();
	public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
	public static final Integer EMPTY=HttpStatus.INTERNAL_SERVER_ERROR.value();
	public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();
	public static final Integer INACTIVE=462;
	public static final Integer EXPIRED=459;
	public static final Integer QUOTA=451;
	public static final String MVNO_ID_FROM_APIGW = "mvnoIdFromApigw";
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
	public static final String KB = "KB";
	public static final String MINUTE = "minute";
	public static final String SECOND = "second";
	public static final String DATA = "Data";
	public static final String BOTH = "Both";
	public static final String NORMAL = "NORMAL";
	public static final String SPECIAL = "SPECIAL";
	public static final String ALL = "ALL";
	public static final String PLAN_GROUP_ALL = "ALL";
	public static final String GROUP_INVOICE_TYPE = "Group";

	public static final String INDEPENDENT_INVOICE_TYPE = "Independent";
	public static final String STAFF_ID_FROM_APIGW = "staffIdFromApigw";
	public static final String MVNO_DELETE_UPDATE_ERROR_MSG = "Permission Denied. Unable to update/delete this record";
	//    public static final String AVOID_SAVE_MULTIPLE_BU = "User with multiple BU access is restricted from 'Save' Operation"
	public static final String AVOID_SAVE_MULTIPLE_BU = "You are not allowed to perform this action, Please contact your system administrator.";

	public static final String TEAM_NOT_DELETED_IF_TICKET_ASSIGN = "Teams not getting deleted, because tickets are assign !!";
	public static final String CAN_NOT_CREATE_SAME_EVENT_HIERARCHY = "Hierarchy with the same event is already exist!!";
	public static final String CUSTOMER = "CUSTOMER";
	public static final String SUBISU = "SUBISU";

	public static final String INVENTORYCOUNTLIMIT="inventory_count_limit";
	public static final String TERMINATION_FAILD = "Customer cannot be Terminated !! Because of termination process could not be satisfied";

	public static final String PLAN_CATEGORY_ALL = "ALL";

	public static final String BUSINESS_PROMOTION = "Business Promotion";

	public static final String NO_ROLE_SELECTED = "Please select at least 1 role";

	public static final String AT_MIDNIGHT = "At_Midnight";

	public static final String ALLOW_NUMBER_OF_TIME_TRAIL = "AllowNumberofTimeTrail";

	public static final String TRIAL_PLAN_PERIOD_THRESHOLD = "trialPlanPeriodThreshold";

	public static final String CURRENT_DATE = "CURRENTDATE";

	public static final String START_DATE = "STARTDATE";

	public static final String GENERATED = "Generated";

	public static final String VOID = "VOID";

	public static final String CANCELLED = "Cancelled";
	public static final String MANDATORY_NOT_NULL_MSG = " is mandatory or must not be Null";

	public static final String STATUS_VALIDATION = " Must be Active or Inactive Only.";
	public static final String PAYMENT_ADDRESS_DETAILS = "Payment Address Details";
	public static final String PERMANENT_ADDRESS_DETAILS = "Permanent Address Details";
	public static final String PRESENT_ADDRESS_DETAILS = "Present Address Details";


	public static final String CREATION = "creation";
	public static final String CHANGE_PLAN = "changeplan";
	public static final String RENEW = "renew";
	public static final String ADD_ON = "addon";

	public static final String REGISTRATION_RENEWAL = "Registration and Renewal";
	public static final String REGISTRATION = "Registration";
	public static final String BANDWIDTH_BOOSTER = "Bandwidthbooster";
	public static final String DTV_ADDON = "DTV Addon";
	public static final String VOLUME_BOOSTER = "Volume Booster";
}
