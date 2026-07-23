package com.savbill.notification.rabbitmq;

import org.springframework.context.annotation.Profile;

@Profile("rabbitmq")
public class RabbitMqConstants 
{
	public static final String EMAIL_ID = "emailId";
    public static final String ALT_EMAIL = "altEmail";
    public static final String ALT_EMAIL_LIST = "altEmailList";
	public static final String USER_NAME = "userName";

    public static final String TICKET_DATA = "ticketData";

    public static final String SUBJECT = "subject";
	public static final String MOBILE_NUMBER = "mobileNumber";
	public static final String VOUCHER = "code";
	public static final String PASSWORD = "password";

	public static final String OTP_STATUS = "otpStatus";
	public static final String OTP = "otp";
    public static final String EMPLOYEE_NAME = "employeeName";
    public static final String PERCENTAGE = "percentage";
    public static final String PAYMENT_URL = "paymentUrl";

    public static final String STATUS = "status";
	public static final String COUNTRY_CODE = "countryCode";
    public static final String  SLICE_CHUNK="sliceChunk";
	public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
	public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
	public static final String DEAD_LETTER_KEY = "deadLetterKey";
	public static final String SAVBILL_EXCHANGE = "savbill.exchange";
	public static final String QUEUE_STAFF_SUCCESS = "staff.success.queue";
	public static final String QUEUE_LOGIN_SUCCESS = "login.success.queue";
	public static final String QUEUE_LOGIN_FAILURE = "login.failure.queue";
	public static final String QUEUE_REGISTRATION_SUCCESS = "registration.success.queue";
    public static final String QUEUE_INSUFFICIENT_WALLET = "insufficient.wallet";
    public static final String QUEUE_AUTO_RENEWAL_PREFERENCE_CHANGED = "auto.renewal.preference.changed";
	public static final String QUEUE_REGISTRATION_FAILURE = "registration.failure.queue";
	public static final String QUEUE_OTP_GENERATION = "otp.generation.queue";

    public static final String QUEUE_OTP_GENERATION_COMMON = "common.otp.generation.queue";
	public static final String QUEUE_ROLE_SUCCESS = "role.success.queue";
	public static final String QUEUE_LOGIN_SUCCESS_WIFI = "login.success.queue.wifi";
	public static final String QUEUE_SEND_VOUCHERCODE = "send.vouchercode.queue";
    public static final String QUEUE_USED_QUOTA = "used.quota.queue";
	public static final String QUEUE_CREATE_SYSTEM_CONFIG_NOTIFICATION = "create.system.config.queue.notification";
	public static final String QUEUE_UPDATE_SYSTEM_CONFIG_NOTIFICATION = "update.system.config.queue.notification";
    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS = "bss.customer.approval.success";
    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_FAIL = "bss.customer.approval.fail";

    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS = "bss.customer.registration.success";
    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL = "bss.customer.registration.fail";

    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS = "bss.customer.renewal.success";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS = "bss.customer.recharge.success";
    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_FAIL = "bss.customer.renewal.fail";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_FAIL = "bss.customer.recharge.fail";

    public static final String QUEUE_BSS_RECHARGE_SUCCESS = "bss.customer.recharge.success";
    public static final String QUEUE_BSS_RECHARGE_FAIL = "bss.customer.recharge.fail";

    public static final String QUEUE_BSS_CUSTOMER_PLAN_EXPIRE = "bss.customer.plan.expire";

    public static final String PREVIOUS_CAF_APPROVER = "previousCafApprover";

    public static final String NEXT_CAF_APPROVER = "nextCafApprover";

    public static final String CAF_APPROVAL_STATUS = "cafApproveStatus";

    public static final String CUSTOMER_APPROVAL_SUCCESS = "Customer Approval Success";
    public static final String CUSTOMER_APPROVAL_FAIL = "Customer Approval Fail";
    public static final String CUSTOMER_RECHARGE_SUCCESS = "Customer Recharge Success";
    public static final String CUSTOMER_RENEW_SUCCESS = "Customer Renew Success";

    public static final String APPROVE_SUCCESS = "Customer Approval Success";
    public static final String APPROVE_REJECT = "Customer Approval Failure";

    public static final String REGISTRATION_SUCCESS = "Registration Success";

    public static final String REGISTRATION_FAIL = "Registration Failure";

    public static final String CUSTOMER_RENEW = "Renewal Success";

    public static final String CUSTOMER_RECHARGE = "Recharge Success";

    public static final String CUST_fullNAME = "name";

    public static final String CUST_NAME = "username";
    public static final String WALLET_BALANCE = "amount";
    public static final String CURRENCY = "Currency";
    public static final String CUST_PASSWORD = "password";
    public static final String TEAM_NAME = "approverTeam";
    public static final String CUST_PLAN = "plan";
    public static final String CUST_OLD_PLAN = "oldPlanName";
    public static final String CUST_NEW_PLAN = "newPlanName";
    public static final String CUST_PLAN_TYPE = "purchaseType";
    public static final String CUST_REQUEST_STATUS = "requestStatus";

    public static final String CUSTMR_NAME = "customerName";
    public static final String CUSTMR_CURRENCY_SYMBOLE= "currencySymbol";
    public static final String CUSTMR_PAYMENT_AMOUNT="paymentAmount";
    public static final String CUSTMR_PAYMENT_MODE="paymentMode";
    public static final String CUSTMR_URL="url";
    public static final String CUSTMR_URL2="url2";
    public static final String CUSTMR_ID="userId";
    public static final String CUSTMR_USER_NAME = "userName";
    public static final String CUSTMR_PAYMENT_RECIPTNO="reciptNo";
    public static final String CUSTMR_PAYMENT_DATE="paymentDate";

    public static final String DOCUMENT_NAME="documentName";
    public static final String EXPIRY_DATE="expiryDate";
    public static final String VALIDITY_DAYS="validityDays";
    public static final String VALIDITY_UNITS="validityUnits";

    public static final String DOCUMENT_ID="invoiceNumber";




    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_LINK="bss.customer.payment.link";
    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS="bss.customer.payment.success";

    public static final String QUEUE_TICKET_ASSIGN_TEAM_SUCCESS="bss.ticket.assign.team.success";
    public static final String CASE_NUMBER="caseNumber";

    public static final String ASSIGN_TEAM_NAME="name";
    public static final String CASE_FOLLOW_UPDATE="nextFollowupDate";

    public static final String QUEUE_BSS_CUSTOMER_DUNNING="bss.customer.dunning";
    public static final String QUEUE_BSS_CUSTOMER_DEACTIVATION = "bss.customer.deactivation";
    public static final String AMOUNT="amount";
    public static final String QUEUE_CUSTOMER_OTP_REGISTRATION = "customer.registration.message";


    public static final String QUEUE_BSS_DOCUMENT_DUNNING_STAFF = "bss.customer.dunning.staff_expire";
    public static final String QUEUE_STAFF_SEND_STATUS = "staff.send.status";
    public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP = "bss.lead.followup";

    //template name
    public static final String FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "Follow Up Reminder For Staff";
    public static final String FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "Follow Up Reminder For Customer";
    public static final String FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "Follow Up OverDue For Staff";
    public static final String FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "Follow Up OverDue For Parent Staff";

    //notification header name
//    public static final String FOLLOW_UP_REMINDER_TEMPLATE_HEADER = "Follow up Reminder";
//    public static final String FOLLOW_UP_OVERDUE_TEMPLATE_HEADER = "Follow up Overdue";

    public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF = "bss.lead.followup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER = "bss.lead.followup.reminder.customer";
    public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF = "bss.lead.followup.overdue.staff";
    public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.lead.followup.overdue.parent.staff";
    public static final String QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF = "bss.lead.nofollowup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF = "bss.lead.nofollowup.reminder.parent.staff";
    public static final String QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF = "bss.nofollowup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF = "bss.nofollowup.reminder.parent.staff";

    public static final String STAFF_PERSON_NAME = "staffPersonName";
    public static final String FOLLOW_UP_TIME = "followupTime";
    public static final String FOLLOW_UP_DATE_TIME = "followupDateTime";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String PARENT_STAFF_PERSON_NAME = "parentStaffPersonName";
    public static final String PARENT_PERSON_NAME = "parentPersonName";
    public static final String FOLLOW_UP_NAME = "followUpName";
    public static final String TASK_FOR = "taskFor";

    public static final String TAT_SUCCESS = "TATNOTIFICATION";
    public static final String EVENT_NAME = "eventName";
    public static final String ASSIGNED_DATE_TIME = "Assigndatetime";
    public static final String START_DATE_TIME = "startDate";
    public static final String END_DATE_TIME = "endDate";
    public static final String QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE= "apiw.send.workflow.action.assign.message";

    public static final String ACTION = "action";

    public static final String QUEUE_SEND_CUSTOMER_STATUS_CHANGE = "apiw.send.customer.status.change";

    public static final String TICKET_NUMBER="ticketnumber";

    public static final String QUEUE_TAT_SEND_PARENT_TO_TEAM = "apigw.tat.send.parent.to.team";
    public static final String QUEUE_TAT_SEND_PARENT_TO_TEAM_FOR_TASK = "apigw.tat.send.parent.to.team.for.task";

    public static final String TAT_TEAM_NAME = "teamName";

    public static final String TICKET_NUMBERS="ticketNumber";

    public static final String QUEUE_SEND_FOLLOWUP_REMARK_MSG = "apigw.send.followup.remark.msg";

    public static final String REMARK = "remark";

    public static final String QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG = "send.problem.domain.change.msg";

    public static final String QUEUE_SEND_TASK_CATEGORY_CHANGE_MSG = "send.task.category.change.msg";

    public static final String OLDVALUE = "oldValue";
    public static final String NEWVALUE = "newValue";


    public static final String QUEUE_TICKET_ETR = "bss.ticket.etr";
    public static final String  QUEUE_TASK_ETR = "bss.task.etr";
    public static final String QUEUE_TICKET_ETR_AUDIT = "bss.ticket.etr.audit";
    public static final String TICKET_ETR_DATE = "additionalDate";
    public static final String TICKET_ETR_TIME = "additionalTime";

    public static final String TICKET_USERNAME= "username";

    public static final String TICKET_SENDER= "sender";

    public static final String TICKET_STATUS= "status";


    public static final String USERNAME = "userName";
    public static final String CUSTOMERID = "customerId";
    public static final String MVNOID = "mvnoId";
    public static final String EMAILID = "emailId";
    public static final String MOBILENO = "mobileNumber";
    public static final String WALLETAMOUNT = "walletPrice";
    public static final String AUTORENEWALPREFERANCE = "Auto-RenewalPreference";


    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF = "bss.customer.caf.followup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER = "bss.customer.caf.followup.reminder.customer";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF = "bss.customer.caf.followup.overdue.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.customer.caf.followup.overdue.parent.staff";


    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF = "bss.ticket.followup.reminder.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER = "bss.ticket.followup.reminder.customer";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF = "bss.ticket.followup.overdue.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.ticket.followup.overdue.parent.staff";

    public static final String TICKET_CREATION = "Ticket Creation";
    public static final String TICKET_CREATION_SUCCESS= "Ticket Creation Success";

    public static final String QUEUE_TICKET_CREATION_SUCCESS="Staff Management";
    public static final String TASK_CREATION_NOTIFICATION= "Task Creation Notification";

    public static  final String TASK_CLOSED_EVENT="Task Close Message";
    public static final String REGISTRATION_DATE = "registrationDate";
    public static final String PLAN_NAME = "planname";
    public static final String PARENT_CUSTOMER_NAME = "parentCustomerName";
    public static final String ACCOUNT_NUMBER = "accountNumber";
    public static final String QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG="bss.ticket.reschedule.success.message";

    public static final String TICKET_RESCHEDULE_MESSAGE = "TicketFollowUpRemark";
    public static final String TICKET_RESCHEDULE_SUCCESS_MSG = "Ticket Reschedule Successful";
    public static final String DATE_TIME = "datetime";
    public static final String TIME_FRAME = "timeframe";
    public static final String CUST_REMARKS = "remarks";
    public static final String CUST_PLANS_NAME = "planname";
    public static final String CUST_DATE = "date";


    public static final String TICKET_TAT_REMINDER_NOTIFICATION_MSG = "Tat Breach Reminder";

    public static final String QUEUE_TICKET_TAT_BREACHED_REMINDER = "bss.ticket.tat.breached.reminder";


    public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG = "Tat Breach Overdue Reminder";

    public static final String QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER = "bss.ticket.tat.overdue.breached.reminder";

    /**Add advance notification added**/

    public static final String USER_NAME_ADD = "Username";
    public static final String EXPIRYDATE = "ExpiryDate";

    public static final String PLANNAME = "planname";

    public static final String QUEUE_DUNNING_ADVANCE_NOTIFICATION = "send.dunning.advance.notification";

    /**Add advance notification ended**/

    /** Add partner document started**/
    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT = "send.dunning.partner.document";

    public static final String QUEUE_CUSTOMER_DUNNING_DOCUMENT = "send.dunning.customer.document";
    public static final String STAFFNAME = "staffName";
    public static final String PARTNERNAME = "partnerName";


    /** Add partner document ended**/


    /** Add partner document deactivation started**/
    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION = "send.dunning.partner.document.deactivation";

    /** Add partner document deactivation ended**/

    /** Add partner document deactivation to staff started**/
    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF = "send.dunning.partner.document.deactivation.staff";

    /** Add partner document deactivation to staff ended**/
    public static final String QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION = "customer.status.inactive.notification";

    public static final String QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION = "customer.document.verification.notification";

    public static final String EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION = "Email notification for customer with lead quotation";

    public static final String QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION = "customer.service.active.notification";
    public static final String QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION = "customer.service.inactive.notification";

    public static final String QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION = "customer.change.password.notification";
    public static final String QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION = "customer.open.address.shifting.notification";
    public static final String QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION = "customer.close.address.shifting.notification";
    public static final String QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION = "customer.payment.verification.notification";
    public static final String QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION = "customer.ticket.close.notification";

    public static final String CUST_MAIL_IDS_FOR_LEAD_QUOTATION_SEND = "custMailIds";

    public static final String QUEUE_APIGW_CUSTOMER_NOTIFICATION = "apigw.customer.notification.queue";

    public static final String TEAM_STAFF = "teamStaff";

    public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "Customer Dunning Deactivation Document";

    public static final String FIRSTNAME = "firstname";
    public static final String LEAD_NO = "leadNo";
    public static final String QUEUE_LEAD_CREATION_NOTIFICATION = "lead.creation.notification";

    public static final String QUEUE_TICKET_TAT_AUDIT = "bss.ticket.tat.audit";

    public static final String QUEUE_TICKET_TAT_SUCCESS_MESSAGE= "bss.ticket.tat.success.message";

    public static final String QUEUE_TASK_TAT_SUCCESS_MESSAGE= "bss.task.tat.success.message";

    public static final String STAFF_PER_NAME= "staffPersonName";

    /**Queue for Ticket Remark to customer started **/
    public static final String QUEUE_TICKET_FOLLOWUP_REMARK_CUSTOMER = "send.ticket_followup_remark_for_customer";
    /**Queue for Ticket Remark to customer ended **/

    /**Queue for Email config send to apigw started **/
    public static final String QUEUE_SEND_EMAIL_CONFIG_TO_APIGW = "send.email.config.apigw";
    /**Queue for Email config send to apigw ended **/

    /**Queue for Customer Quota notification to customer started **/
    public static final String QUEUE_SEND_QUOTA_NOTIFICATION_CUSTOMER = "send.cust_quota_notification_customer";

    public static final String QUEUE_SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER = "send.cust_quota_exhuast_notification_customer";

    /**Queue for Customer Quota notification to customer ended **/


    public static final String QUEUE_MVNO_CREATE_DATA_SHARE_NOTIFICATION_MICROSERVICE = "queue.mvno.create.data.share.notification.microservice";
    public static final String QUEUE_MVNO_UPDATE_DATA_SHARE_NOTIFICATION_MICROSERVICE = "queue.mvno.update.data.share.notification.microservice";

    public static final String QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_NOTIFICATION = "queue.businessunit.create.data.share.notification";
    public static final String QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_NOTIFICATION = "queue.businessunit.update.data.share.notification";

    /**Queue for Ticket External Remark to customer started **/
    public static final String QUEUE_EXTERNAL_TICKET_REMARK_TO_CUSTOMER = "send.cust_external_remark_customer";
    /**Queue for Ticket External Remark to customer ended **/
    /**Queue for Ticket External Remark to customer started **/
    public static final String QUEUE_EXTERNAL_TASK_REMARK = "send.external.task.remark";
    /**Queue for Ticket External Remark to customer ended **/

    public static final String CASE_TITLE="caseTitle";
    public static final String CASE_PRIORITY = "casePriority";
    public static final String TEAM="team";

    /**receive email for invoice started**/
    public static final String QUEUE_SEND_INVOICE_TO_NOTIFICATION = "queue.revenue.send.invoice.to.notification";

    /** received email for inventory send approval to staff */
    public static final String QUEUE_INVENTORY_SEND_APPROVAL_TO_STAFF_TO_NOTIFICATION="queue.send.inventory.inward.approval.to.notification";
    /** received email for inventory send approval to staff */
    public static final String QUEUE_INVENTORY_REQUEST_TO_NOTIFICATION="queue.send.inventory.inward.request.to.notification";
    /** received email for inventory send approval to staff */
    public static final String QUEUE_INVENTORY_FULFILMENT_TO_NOTIFICATION="queue.send.inventory.inward.fulfilment.to.notification";

    public static final String QUEUE_INVENTORY_THRESHOLD_NOTIFICATION = "queue.send.inventory.threshold.tonotification";
    /**receive email for invoice ended**/

    /**receive email for ticket alert to staff started**/
    public static final String QUEUE_TICKET_ALERT_TO_STAFF = "send.ticket_alert_staff";
    /**receive email for ticket alert to staff ended**/

    /**receive email for ticket alert to staff started**/
    public static final String QUEUE_TASK_ALERT_TO_STAFF = "send.task.alert.staff";
    /**receive email for ticket alert to staff ended**/
    /**RabbitMq for sending  unregister customer inquirey to customer started**/

    public static final String QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER = "send.immediate_attention_to_unregistred_customer";
    /**RabbitMq for sending unregister customer inquirey to customer ended**/

    /**RabbitMq for sending  register customer inquirey to customer started**/

    public static final String QUEUE_IMMEDIATE_ATTENTION_TO_REGISTRED_CUSTOMER = "send.immediate_attention_to_registred_customer";
    /**RabbitMq for sending register customer inquirey to customer ended**/

    /**RabbitMq for sending  Unregister customer inquirey to staff started**/

    public static final String QUEUE_IMMEDIATE_ATTENTION_TO_UNREGISTRED_CUSTOMER_STAFF = "send.immediate_attention_to_unregistred_customer_to_staff";
    /**RabbitMq for sending Unregister customer inquirey to staff ended**/

    /**RabbitMq for sending unpick ticket alert to staff started**/
    public static final String QUEUE_UNPICK_TICKET_ALERT_TO_STAFF = "send.unpick_ticket_alert_staff";
    public static final String QUEUE_CAF_TAT_SUCCESS_MESSAGE= "bss.caf.tat.success.message";
    public static final String QUEUE_TREMINATION_TAT_SUCCESS_MESSAGE= "bss.termination.tat.success.message";
    public static final String QUEUE_LEAD_TAT_SUCCESS_MESSAGE= "bss.lead.tat.success.message";
    /**RabbitMq for sending unpick ticket alert to staff ended**/

    /**Rabbitmq for sending mvno document dunning message started**/
    public static final String QUEUE_SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION = "queue_send_mvno_dunning_message_to_notification";
    /**Rabbitmq for sending mvno document dunning message ended**/

    public static final String QUEUE_SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION = "queue_send_mvno_deactivation_message_to_notification";
    public static final String QUEUE_SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION = "queue_send_mvno_paayment_advance_notification";
    public static final String QUEUE_SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION = "queue_send_mvno_paayment_remainder_notification";
    public static final String QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_NOTIFICATION_ISP = "queue.create.mvno.common.apigw.to.notification.isp";
    public static final String QUEUE_SEND_PLAN_EXPIRY_NOTIFICATION = "queue.plan.expiry.notification";
    public static final String QUEUE_SEND_USED_PORT_NOTIFICATION_INVENTORY_TO_NOTIFICATION = "queue.create.used.port.inventory.to.notification";

    public static final String QUEUE_SEND_CHANGE_PLAN_NOTIFICATION = "queue.change.plan.notification";
    public static final String QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION = "queue.change.plan.data.share.notification";

    public static final String TASK_NUMBER = "caseNumber";

    public static final String TASK_STATUS = "caseStatus";

    public static final String TASK_PRIORITY = "casePriority";


    public static final String TASK_REMARK = "caseRemark";

    public static final String TASK_LOGO = "urlLogo";

    public static final String TASK_URL = "taskURL";
    public static final String FOLLOWUP_TASK_MSG= "Task Followup Remark";
    public static  final String TASK_RESCHEDUKE_NOTIFICATION="task reaschedule";
    public static final String TASK_FOR_STAFF = "Staff";
    public static final String TASK_FOR_CUSTOMER = "Customer";
    public static final String TASK = "Task";

    public static final String USER_TYPE = "type";



    public static final String TASK_UPDATE = "Task Update";

    public static final String DUE_DATE = "dueDate";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String SUB_TOTAL = "subTotal";
    public static final String TAX_AMOUNT = "taxAmount";
    public static final String TAX_PERCENTAGE = "taxPercentage";
    public static final String TOTAL_DUE = "totalDue";
    public static final String CREDIT_BALANCE = "credit";
    public static final String PLAN_AMOUNT = "planAmount";


    public static final String QUEUE_BSS_CHILD_CUSTOMER_REGISTRATION_SUCCESS = "bss.child.customer.registration.success";
}
