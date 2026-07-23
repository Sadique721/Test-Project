package com.savbill.notification.utils;


import org.springframework.http.HttpStatus;

public class NotificationConstants {
	private NotificationConstants() {
		 throw new IllegalStateException("Utility class");
	}
	public static final Integer SUCCESS=HttpStatus.OK.value();
	public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
	public static  final Integer EXPECTATION_FAILED=HttpStatus.EXPECTATION_FAILED.value();
	public static final Integer INTERNAL_SERVER_ERROR=HttpStatus.INTERNAL_SERVER_ERROR.value();
	public static final Integer NULL_VALUE=HttpStatus.NOT_FOUND.value();
	public static final String ERROR_TAG="ERROR";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String MESSAGE = "message";
	public static final String BLANK_STRING = "string";
	public static final String IN_ACTIVE = "Inactive";
	public static final String ACTIVE = "Active";
	public static final String VALIDATION_REASON = "ValidationReason";
	public static final String BASIC_STRING_MSG = "BasicStringMsg";
	public static final String BASIC_NUMERIC_MSG = "BasicNumericMsg";
	public static final String VALIDATION_REASON_BASIC_STRING_MESSAGE="Null, blank value and 'string' are not allowed";
	public static final String VALIDATION_REASON_BASIC_NUMERIC_MESSAGE="Null and 0 are not allowed";
	public static final String FROM_NUMBER="FROM_NUMBER";
	//public static final String FROM_EMAIL_ADDRESS="savbillnotificationfortesting@gmail.com";
	//public static final String FROM_EMAIL_PASSWORD="savbill@123";
	public static final String ACCOUNT_SID = "ACCOUNT_SID";
	public static final String AUTH_TOKEN = "AUTH_TOKEN";
	public static final String AUTH_PARAM = "mail.smtp.auth";
	public static final String STARTTLS_PARAM = "mail.smtp.starttls.enable";
	public static final String SSL_PARAM = "mail.smtp.ssl.enable";
	public static final String AUTH_TYPE_VALUE ="true";
	public static final String HOST_PARAM = "mail.smtp.host";
	public static final String SMTP_SOCKETFACTORY_PORT = "mail.smtp.socketFactory.port";
	public static final String SMTP_SOCKETFACTORY_CLASS = "mail.smtp.socketFactory.class";
	public static final String SSL_SOCKETFACTORY = "javax.net.ssl.SSLSocketFactory";
	public static final String SMTP_TIMEOUT = "mail.smtp.timeout";
	public static final String SMTP_SSL_TRUST = "mail.smtp.ssl.trust";
	public static final String PORT_PARAM = "mail.smtp.port";
	public static final String SENDER = "Savbill NetTech";
	public static final String WEB = "Savbill";
	public static final String LOGIN_FAILURE_EVENT = "Login Failure";
	public static final String LOGIN_SUCCESS_EVENT = "Login Success";
	public static final String CUSTOMER_REGISTRATION_SUCCESS_EVENT="Registration Success";
	public static final String CHILD_CUSTOMER_REGISTRATION_SUCCESS_EVENT="Child Customer Registration Success";
	public static final String CUSTOMER_REGISTRATION_FAILURE_EVENT="Registration Failure";
	public static final String OTP_GENERATED_EVENT="OTP Generated";
	public static final String SEND_VOUCHERCODE_EVENT="Voucher Code";
    public static final String USED_QUOTA_EVENT="Used Quota";
	public static final String USER_NAME = "userName";
	public static final String TYPE_FETCH = "fetch";
	public static final String TYPE_UPDATE = "update";
	public static final String TYPE_DELETE = "delete";
	public static final String TYPE_CREATE = "create";
	public static final String TYPE_LOGIN = "login";

	public static final String UI = "ui";
	public static final String TYPE_LOGOUT = "logout";
	public static final String TYPE_VALIDATE = "validate";
	public static final String TYPE = "type";
	public static final String TRACE_ID = "traceId";
	public static final String SPAN_ID = "spanId";
	public static final String START_TLS = "StartTLS";

	public static final String Email_UPDATEKEY="eventId";
	public static final String Email_PRIMARYKEY="emailId";
	public static final String Email_TABLENAME="TBLMEMAIL";

	public static final String EmailConfig_UPDATEKEY="password";
	public static final String EmailConfig_PRIMARYKEY="emailConfigId";
	public static final String EmailConfig_TABLENAME="TBLMEMAILCONFIG";

	//public static final String Event_UPDATEKEY="userName";no need
	public static final String Event_PRIMARYKEY="eventId";
	public static final String Event_TABLENAME="TBLMEVENT";

	public static final String Role_UNIQUEKEY="name";
	public static final String Role_PRIMARYKEY="roleId";
	public static final String Role_TABLENAME="TBLMROLE";

	public static final String RoleScreens_UNIQUEKEY="name";
	public static final String RoleScreens_PRIMARYKEY="roleScreenId";
	public static final String RoleScreens_TABLENAME="TBLSCREENSROLEMAPPING";

	//public static final String Screen_UNIQUEKEY="name";
	public static final String Screen_PRIMARYKEY="screenId";
	public static final String Screen_TABLENAME="TBLSCREENS";

	public static final String Sms_PRIMARYKEY="smsId";
	public static final String Sms_TABLENAME="TBLMSMS";


	public static final String SmsConfig_PRIMARYKEY="smsConfigId";
	public static final String SmsConfig_TABLENAME="TBLMSMSCONFIG";

	public static final String SmsConfigMapping_UPDATEKEY="smsConfigId";
	public static final String SmsConfigMapping_PRIMARYKEY="smsConfigMappingId";
	public static final String SmsConfigMapping_TABLENAME="TBLMSMSCONFIGMAPPING";


	public static final String Staff_PRIMARYKEY="staffId";
	public static final String Staff_TABLENAME="TBLMSTAFF";

	public static final String Template_UPDATEKEY="eventId";
	public static final String Template_PRIMARYKEY="templateId";
	public static final String Template_TABLENAME="TBLMTEMPLATE";

	public static final String CUST_APPROVAL = "Customer Approval Success";
	public static final String CUST_REJECT = "Customer Approval Failure";
	public static final String CUST_REG_SUCCESS = "Registration Success";
	public static final String CUST_REG_FAIL = "Registration Failure";
	public static final String CUST_RENEW_SUCCESS = "Renewal Success";
	public static final String CUST_RECHARGE_SUCCESS = "Recharge Success";

	public static final String CUSTOMER_PAYMENT_LINK = "Payment Link";
	public static final String CUSTOMER_PAYMENT_SUCCESS = "Payment Success";

	public static final String TICKET_ASSIGN_SUCCESS="Ticket";
	public static final String CUSTOMER_DUNNING_TEMPLATE_HEADER = "Payment Reminder";
	public static final String CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER = "Welcome Message 2";
	public static final String CUSTOMER_DEACTIVATION_TEMPLATE_HEADER = "Customer Deactivation";
	public static final String EXPIRED_DOCUMENT_TEMPLATE_HEADER = "Expired Document";

	public static final String FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "Follow up Reminder For Staff";
	public static final String FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "Follow up Reminder For Customer";
	public static final String FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF = "Follow up Overdue For Staff";
	public static final String FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "Follow up Overdue For Parent Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "No Lead FollowUp Reminder for Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No Lead FollowUp Reminder for ParentStaff";
	public static final String NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "No FollowUp Reminder for Staff";
	public static final String NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No FollowUp Reminder for ParentStaff";
	public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Reminder For Staff";
	public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "CAF Follow up Reminder For Customer";
	public static final String CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Overdue For Staff";
	public static final String CAF_FOLLOW_UP_OVER_DUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "CAF Follow up Overdue For Parent Staff";

	public static final String WORKFLOW_ACTION_ASSIGN = "Workflow Assign Action";

	public static final String CUSTOMER_STATUS_CHANGE_EVENT = "Customer Ticket Status Change";
	public static final String STAFF_STATUS_CHANGE_TEMPLATE_HEADER = "Staff Status Change";

	public static final String TAT_NOTIFICATION_TO_TEAM = "TATNOTIFICATIONTOTEAM";
	public static final String TAT_NOTIFICATION_TO_TEAM_FOR_TASK = "TATNOTIFICATIONTOTEAMFORTASK";

	public static final String TAT_NOTIFICATION = "TATNOTIFICATION";

	public static final String TASK_TAT_NOTIFICATION = "TASKTATNOTIFICATION";
	public static  final String FOLLOWUP_REMARK_MSG = "TicketFollowUpRemark";

	public static  final String PROBLEM_DOMAIN_EVENTNAME = "TicketDomainChangeMsg";

	public static  final String CATEGORY_CHANGE_EVENTNAME_FOR_TASK = "TaskCategoryChangeMsg";


	public static  final String TICKET_ETR = "Ticket ETR";

	public static  final String TICKET_ETR_DYNAMIC = "Ticket ETR Dynamic";

	public static  final String TASK_ETR = "Task ETR";

	public static  final String TASK_ETR_DYNAMIC = "Task ETR Dynamic";



	public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "TICKET Follow up Reminder For Customer";
	public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Reminder For Staff";
	public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Overdue For Staff";
	public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "TICKET Follow up Overdue For Parent Staff";

	public static  final String TICKET_CREATION = "Ticket Creation";

	public static final String  TICKET_RESCHEDULE_EVENT = "TicketFollowUpRemark";

	public static final String TICKET_TAT_REMINDER_NOTIFICATION = "TatBreachedFollowup";
	public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION = "TatBreachedOverDueFollowup";

	/**Advance Notification Constant**/
	public static final String CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER = "Plan Expiry Reminder";
	public static final String CUSTOMER_STATUS_INACTIVATE_EVENT = "Customer Status InActive";

	/**Partner Document  Constant**/

	public static final String PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Partner Document  Reminder";

	public static final String CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Customer Document Reminder";

	/**Partner Document  Constant**/

	public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "Partner Document Deactivation Reminder";

	/**Partner Document Staff notification Constant**/
	public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER = "Staff Document Deactivation Reminder";
	public static final String CUSTOMER_DOCUMENT_VERIFICATION_EVENT = "Customer Document Verification";
	public static final String EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION = "Email notification for customer with lead quotation";
	public static final String CUSTOMER_SERVICE_ACTIVE_EVENT = "Customer Service Active";
	public static final String CUSTOMER_SERVICE_INACTIVE_EVENT = "Customer Service InActive";
	public static final String CUSTOMER_CHANGE_PASSWORD_EVENT = "Customer Change Password";
	public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT = "Customer Open Address Shifting";
	public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT = "Customer Close Address Shifting";
	public static final String CUSTOMER_PAYMENT_VERIFICATION_EVENT = "Customer Payment Verification";

	public static final String CUSTOMER_TICKET_CLOSE_EVENT = "Customer Ticket Close";

	public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "Customer Dunning Deactivation Document";
	public static final String LEAD_CREATION_EVENT = "Lead Creation Success";

	/**Notification Constant for Ticket Remark to customer event started**/
	public static final String TICKET_REMARK_CUSTOMER_EVENT = "Ticket Remark Message";
	/**Notification Constant for Ticket Remark to customer event ended**/

	/**Notification Constant for Customer Quota to customer event started**/
	public static final String CUSTOMER_QUOTA_USAGE_NOTIFICATION_EVENT = "Quota Usage";

	public static final String CUSTOMER_QUOTA_EXHAUST_NOTIFICATION_EVENT = "Quota Exhaust";

	/**Notification Constant for Customer Quota to customer event ended**/

	/**Notification Constant for Ticket Extrenal Remark to customer event started**/
	public static final String TICKET_EXTERNAL_REMARK_CUSTOMER_EVENT = "Ticket External Remark Message";
	/**Notification Constant for Ticket Extrenal Remark to customer event ended**/

	/**Notification Constant for Ticket Extrenal Remark to customer event started**/
	public static final String TASK_EXTERNAL_REMARK_EVENT = "Task External Remark Message";
	/**Notification Constant for Ticket Extrenal Remark to customer event ended**/

	/**Notification Constant for  customer invoice event started**/
	public static final String CUSTOMER_INVOICE_EVENT = "Customer Invoice Message";
	/**Notification Constant for  customer invoice event ended**/

	/**Notification Constant for  ticket alert started**/
	public static final String STAFF_TICKET_ALERT_EVENT = "Ticket Alert For Staff";
	/**Notification Constant for  ticket alert ended**/


	/**Notification Constant for  ticket alert started**/
	public static final String STAFF_TASK_ALERT_EVENT = "Task Alert For Staff";
	/**Notification Constant for  ticket alert ended**/

	/**Notification Constant for  Immediate attention rewuired to unregitster customer started**/
	public static final String IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER = "UnRegister Customer Inquire Message";
	/**Notification Constant for  Immediate attention rewuired to unregitster customer started**/

	/**Notification Constant for  Immediate attention rewuired to regitster customer started**/
	public static final String IMMEDIATE_ATTENTION_EMAIL_TO_REGISTERED_CUSTOMER = "Register Customer Inquire Message";
	/**Notification Constant for  Immediate attention rewuired to regitster customer started**/

	/**Notification Constant for  Immediate attention rewuired to regitster customer started**/
	public static final String IMMEDIATE_ATTENTION_EMAIL_TO_UNREGISTERED_CUSTOMER_STAFF = "UnRegister Customer Inquire To Staff Message";
	/**Notification Constant for  Immediate attention rewuired to regitster customer started**/


	/**Notification Constant for UnPick ticket alert started**/
	public static final String STAFF_UNPICK_TICKET_ALERT_EVENT = "Open Ticket Alert For Staff";
	/**Notification Constant for UnPick ticket alert ended**/

	/**Notification Constant for Mvno Dunning message started**/
	public static final String MVNO_DOCUMENT_DUNNING_EVENT = "Mvno Document Dunning";
	public static final String MVNO_DEACTIVATION_EVENT = "Mvno Deactivation";
	public static final String MVNO_PAYMENT_REMAINDER_EVENT = "Mvno Payment Expiry Reminder";
	public static final String MVNO_PAYMENT_EXPIRY_EVENT = "Mvno Payment Expiry";
	/**Notification Constant for Mvno Dunning message ended**/





	public static final String AVOID_SAVE_MULTIPLE_BU = "You are not allowed to perform this action, Please contact your system administrator.";


	/**Notification Constant for IWF Notification started**/
	public static final String EMAIL_ID = "emailId";
	public static final String EMAIL_CONTENT = "emailContent";

	public static final String EVENT_ID = "eventId";

	public static final String BASIC_MAIL_SENDING = "Basic Mail Sending";

	public static final String MOBILE = "mobile";

	public static final String FIRSTNAME = "firstName";

	public static final String ALT_EMAIL = "alternativeEmailId";
	public static final String BCC_EMAIL = "bccEmailId";

	public static final String TEMPLATE_NOT_FOUND = "Template Not Found";
	public static final String IWF_NOTIFICATION_GROUP = "IwfNotificationGroup";

	public static final String COMMMON_NOTIFICATION_GROUP = "CommonNotificationGroup";
	public static final String KAFKA_COMMON_NOTIFICATION_TOPIC = "notification";
	public static final String TOPIC_NAME = "NOTIFICATIONCOMMON";

	public static final String MAIL_SENDING_WITH_DYNAMIC_FORMATTING = "Dynamic HTML Mail Sending";

	public static final String DYNAMIC_HTML = "dynamicHTML";

	public static final String FILTERED_ATTRIBUTES = "filteredAttributes";

	public static final String TEMPLATE_STRUCTURE_FILE = "demo3.ftl";

	public static final String SENDER_ENTITY_NAME = "SENDER";

	public static final String FIRST_NAME_VAL = "Dear Client";

	public static final String BOOL_TRUE_AS_STR = "true";
	public static final String BOOL_FALSE_AS_STR = "false";
	public static final String GENERATE_PASS_URL = "genPassUrl";
	public static final String PASS_USER_NAME = "username";

	public static final String FILE_PATH = "filePath";
	public static final String CURRENT_CHECK_SUM = "currentCheckSum";
	public static final String NEW_CHECK_SUM = "newCheckSum";
	public static final String MAIL_SUBJECT = "Test Mail Process Success";
	public static final String QUEUE_INSUFFICIENT_WALLET = "insufficient.wallet";
	public static final String QUEUE_AUTO_RENEWAL_PREFERENCE_CHANGED = "auto.renewal.preference.changed";

	public interface NotificationAttributes{
		public static final String DEVICE_DRIVER_NAME = "deviceDriverName";
		public static final String DEVICE_DRIVER = "driverId";
		public static final String DEVICE_PORT = "port";
		public static final String DEVICE_TIME_INTERVAL = "timeInterval";
		public static final String SOURCE_IP = "sourceIp";
		public static final String LOCATIONNAME = "locationName";
		public static final String DEVICETYPE = "deviceType";
		public static final String GENERATE_PASS_URL = "genPassUrl";
		public static final String PASS_USER_NAME = "username";
	}
	public interface EventType {
		public static final String EVENT_TYPE_SCHEDULE = "Schedule";
		public static final String EVENT_TYPE_TRIGGER = "Trigger";
	}
	public interface IntervalTimeType {
		public static final String INTERVAL_TIME_TYPE_MINUTE = "Minutes";
		public static final String INTERVAL_TIME_TYPE_HOUR = "Hour";
	}
	public interface ConstraintType {
		public static final String CONSTRAINT_TYPE_EXACT_MATCH = "Exact Match";
		public static final String CONSTRAINT_TYPE_REGEX_BASED = "Regex Based";
	}
	public interface ContentType {
		public static final String CONTENT_TYPE_MANUAL = "TEXT";
		public static final String CONTENT_TYPE_FTL_BASED = "FTL";
	}
	public interface ServiceType {
		public static final String SERVICE_TYPE_BSS = "BSS";
		public static final String SERVICE_TYPE_IWF = "IWF";
	}

	public interface ApplicationName {
		public static final String ENRICHMENT_APPLICATION = "ENRICHMENT";
		public static final String COLLECTION_APPLICATION = "collection-agent";

		public static final String INDEX_COORDINATION_APPLICATION = "index-coordination-node";
		public static final String APIGATEWAY_COMMON_APPLICATION = "/SavbillApiGateWayCommon";
	}

	public interface NotificationStatus {
		public static final String SENT = "Sent";
		public static final String FAILURE = "Failure";
	}
	public interface SmsSearchEnum {
		public static final String ANY ="any";
		public static final String DATE="date";
		public static final String SOURCE_NAME="sourceName";
		public static final String MVNO_ID="mvnoId";
		public static final String STATUS="status";
		public static final String SERVICE_TYPE="serviceType";
		public static final String MOBILE_NO="mobileNo";
	}
	/**Notification Constant for IWF Notification ended**/

	public interface EmailConfigSearch {
		public static final String ANY = "any";
		public static final String DATE = "date";
		public static final String MVNO_ID = "mvnoId";
		public static final String SERVICE_TYPE = "serviceType";
		public static final String USER_NAME = "userName";
		public static final String HOST_SERVER = "hostServer";
		public static final String PORT = "port";
		public static final String BU_ID = "buId";

	}
	public static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
	public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
	public static final String AUTHORIZATION_HEADER_STRING = "Authorization";

	public static final String PLAN_EXPIRY_EVENT="Plan Expiry";
	public static final String CHANGE_PLAN_EVENT="Change Plan";

	public interface API_Response_Message {
		public final String CREATED_SUCCESSFULLY = "Successfully Created";
		public final String UPDATED_SUCCESSFULLY = "Successfully Updated";
		public final String DELETED_SUCCESSFULLY = "Successfully Deleted";
		public final String FETCHED_SUCCESSFULLY = "Successfully Fetched";
		public final String NO_RECORDS_FOUND = "No Records Found!";
		public final String ALREADY_EXIST = " already exists!";
		public final String IN_USED = " in used!";
	}

	public static final String LOGIN_OTP_EVENT="Login OTP Event";


	public static final String TASK_CREATION_NOTIFICATION= "Task Creation Notification";
	public static final String FOLLOWUP_TASK_MSG= "Task Followup Remark";
	public static final String TASK_REMARK_EVENT= "Task Remark Message";
	public static  final String TASK_RESCHEDUKE_NOTIFICATION="task reaschedule";
	public static  final String TASK_RESCHEDUKE_EVENT="Task Reschedule";

	public static  final String TASK_CLOSED_EVENT="Task Close Message";
	public static  final String TASK="Task";
	public static final String TASK_UPDATE = "Task Update";

	public static final String INSUFFICIENT_WALLET_EVENT = "Insufficient Wallet";
	public static final String AUTO_RENEWAL_PREFERENCE_CHANGED_EVENT = "Auto-Renewal Preference Changed";
    public interface Inventory_Event_Name {
        public static final String INVENTORY_ASSIGNMENT_SUCCESS_EVENT = "Inventory Assignment For Staff";
        public static final String INVENTORY_REQUEST_EVENT="Inventory Request";
        public static final String INVENTORY_FULFILMENT_EVENT="Inventory Fulfilment";
		public static final String INVENTORY_THRESHOLD_EVENT = "Inventory Threshold";
        public static final String DEVICE_INPUT_PORT_CONSUMED_PERCENTAGE_EVENT="Consumed Port Percentage Event";
    }


}
