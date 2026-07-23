package com.savbill.taskmanagement.rabbitmq.rqconstants;

public class RMQConstants {



    public static final String SAVBILL_EXCHANGE="savbill.exchange";

    public static final String DEAD_LETTER_QUEUE = "deadLetter.queue";
    public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
    public static final String DEAD_LETTER_KEY = "deadLetterKey";

    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String PASSWORD = "password";
    public static final String USER_NAME = "username";
    public static final String EMAIL_ID = "emailId";
    public static final String EMAIL_ADDRESS = "emailAddress";

    public static final String SOURCE_NAME_SAVBILL_BSS_GATEWAY = "Savbill BSS API GATEWAY";
    public static final String MVNO_ID = "mvnoId";
    public static final String  TEAM_NAME = "TeamName";

    public static final String  SLICE_CHUNK="slicechunk";



    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_SUCCESS="bss.customer.approval.success";
    public static final String QUEUE_BSS_CUSTOMER_APPROVAL_FAIL="bss.customer.approval.fail";

    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_SUCCESS="bss.customer.registration.success";
    public static final String QUEUE_BSS_CUSTOMER_REGISTRATION_FAIL="bss.customer.registration.fail";

    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_SUCCESS="bss.customer.renewal.success";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_SUCCESS="bss.customer.recharge.success";
    public static final String QUEUE_BSS_CUSTOMER_RENEWAL_FAIL="bss.customer.renewal.fail";
    public static final String QUEUE_BSS_CUSTOMER_RECHARGE_FAIL="bss.customer.recharge.fail";

    public static final String QUEUE_BSS_RECHARGE_SUCCESS="bss.customer.recharge.success";
    public static final String QUEUE_BSS_RECHARGE_FAIL="bss.customer.recharge.fail";

    public static final String QUEUE_BSS_CUSTOMER_PLAN_EXPIRE="bss.customer.plan.expire";

    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_LINK="bss.customer.payment.link";
    public static final String QUEUE_BSS_CUSTOMER_PAYMENT_SUCCESS="bss.customer.payment.success";


    public static final String PREVIOUS_CAF_APPROVER="previousCafApprover";

    public static final String NEXT_CAF_APPROVER="nextCafApprover";

    public static final String CAF_APPROVAL_STATUS="cafApproveStatus";


    public static final String CUSTOMER_APPROVAL_SUCCESS="Customer Approval Success";
    public static final String CUSTOMER_APPROVAL_FAIL="Customer Approval Fail";
    public static final String CUSTOMER_RECHARGE_SUCCESS="Customer Recharge Success";
    public static final String CUSTOMER_RENEW_SUCCESS="Customer Renew Success";

    public static final String APPROVE_SUCCESS = "Customer Approval Success";
    public static final String APPROVE_REJECT="Customer Approval Failure";

    public static final String REGISTRATION_SUCCESS= "Registration Success";

    public static final String REGISTRATION_FAIL = "Registration Failure";

    public static final String CUSTOMER_RENEW = "Renewal Success";

    public static final String CUSTOMER_RECHARGE = "Recharge Success";


    public static final String CUSTOMER_PAYMENT_LINK = "Payment Link";
    public static final String CUSTOMER_PAYMENT_SUCCESS = "Payment Success";

    public static final String CUSTOMER_STATUS_NEW_ACTIVATION  = "NewActivation";
    public static final String CUSTOMER_STATUS_ACTIVE  = "Active";





    public static final String QUEUE_APIGW_CUSTOMER = "apigw.customer.queue";
    public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE = "apigw.customer.status.queue";
	public static final String QUEUE_APIGW_CUSTOMER_MAC_MAPPING = "apigw.customer.mac.mapping";
	public static final String QUEUE_APIGW_CUSTOMER_PACKAGE_REL = "apigw.customer.package.rel";
    public static final String QUEUE_APIGW_POSTPAIDPLAN = "apigw.plan";
    public static final String QUEUE_APIGW_QOS_POLICY = "apigw.qospolicy";
    public static final String QUEUE_APIGW_CUST_REPLY = "apigw.custreply";
    public static final String CURRENCY_SYMBOLE = "Rs.";

    public static final String TICKET_SUCCESS = "Ticket";
    public static final String TICKET_ASSIGN_SUCCESS = "Ticket";
    public static final String QUEUE_TICKET_ASSIGN_TEAM_SUCCESS = "bss.ticket.assign.team.success";

    public static final String QUEUE_STAFFUSER_SEND_RADIUS_SUCCESS = "staff_create_from_bss";
    public static final String QUEUE_STAFFUSER_SEND_TASK_MGMT_SUCCESS = "staff_create_from_bss_to_task_mgmt";

    public static final String QUEUE_SERVICE_AREA_SEND_RADIUS_SUCCESS = "service_area_created_from_bss";

    public static final String QUEUE_UPDATE_QUOTA = "update.bssquota.queue";

    public static final String CUSTOMER_DUNNING_TEMPLATE = "Customer Dunning";
    public static final String CUSTOMER_DEACTIVATION_TEMPLATE = "Customer Deactivation";
    public static final String STAFF_EXPIRED_TEMPLATE = "Staff Expired";
    public static final String STAFF_EXPIRED_TEMPLATE_HEADER = "Staff Expired";
    public static final String STAFF_STATUS_CHANGE_TEMPLATE = "Staff Status Change";
    public static final String STAFF_STATUS_CHANGE_TEMPLATE_HEADER = "Staff Status Change";


    public static final String QUEUE_BSS_CUSTOMER_DUNNING = "bss.customer.dunning";


    public static final String QUEUE_BSS_CUSTOMER_DEACTIVATION = "bss.customer.deactivation";
    public static final String CUSTOMER_DUNNING_TEMPLATE_HEADER = "Payment Reminder";

    public static final String CUSTOMER_DEACTIVATION_TEMPLATE_HEADER= "Customer Deactivation";
    
	public static final String QUEUE_RADIUS_CUST_MAC_ADD = "radius.add.mac";

    //OTPCOnstast
    public static final String OTP = "otp";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String QUEUE_OTP_GENERATION = "otp.generation.queue";
    //Time Base Policy
    public static final String QUEUE_APIGW_TIME_BASE_POLICY = "apigw.timebasepolicy";
    public static final String QUEUE_UPDATE_CUSTOMER_QUOTA = "update.customer.quota";
    public static final String QUEUE_BILLING_INVOICE = "billing.invoice";

//    public static final String QUEUE_UPDATE_CUSTOMER_QUOTA = "update.customer.quota";

    public static final String QUEUE_CUSTOMER_OTP_REGISTRATION = "customer.registration.message";

    public static final String CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER = "Welcome Message 2";
    public static final String CUSTOMER_OTP_REGISTRATION_TEMPLATE = "Welcome Message 2";

    public static final String QUEUE_UPDATE_CUSTOMER_PASSWORD = "update.customer.password";

//Lead Mgmt workfloq queues

//    public static final String QUEUE_FIND_STAFF_FOR_LEAD = "bss.staff.find.for.lead";
//
//    public static final String QUEUE_COUNT_FOR_STAFF ="bss.count.for.staff";



    public static final String QUEUE_LEAD_MGMT_INIT_DATA ="bss.lead.mgmt.init.data";

    //Staff expired Document
    public static final String QUEUE_BSS_DOCUMENT_DUNNING_STAFF = "bss.customer.dunning.staff_expire";
    public static final String EXPIRED_DOCUMENT_TEMPLATE_HEADER = "Expired Document";
    public static final String EXPIRED_DOCUMENT_TEMPLATE = "Expired Document";

    //user create
    public static final String QUEUE_ROLE = "bss.role";
    public static final String QUEUE_USER = "bss.user";

    //businessunit create
    public static final String QUEUE_BUSINESS_UNIT = "bss.business.unit";

    public static final String QUEUE_SUB_BUSINESS_UNIT = "bss.subbusiness.unit";
    public static final String QUEUE_SEND_APPROVER_DETAIL = "send.aprover.detail";
    public static final String QUEUE_SEND_APPROVER_UPDATE_DETAIL = "send.aprover.update.detail";
    public static final String QUEUE_SEND_UPDATE_LEAD_INFO = "send.updated.lead.info";

    public static final String QUEUE_SEND_LEAD_STATUS_INFO = "send.lead.status.info";

    public static final String QUEUE_SEND_LEAD_STATUS_DTO = "send.lead.status.dto";

    public static final String QUEUE_SEND_NOTIFICATION_TAT = "send.tat.notification";

    public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY = "apigw.create.timebasepolicy";

    public static final String QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS = "apigw.create.timebasepolicydetails";

    public static final String TAT_SUCCESS = "TATNOTIFICATION";

    public static final String QUEUE_SEND_CUSTOMER_CAF_POJO = "send.customer.caf.pojo";
    
    public static final String QUEUE_APIGW_SEND_BRANCH = "apigw.send.branch";
    
    public static final String QUEUE_APIGW_SEND_PARTNER = "apigw.send.partner";
    
    public static final String QUEUE_APIGW_SEND_SERVICE_AREA = "apigw.send.servicearea";
    
    public static final String QUEUE_APIGW_SEND_CUSTOMER = "apigw.send.customer";

    public static final String QUEUE_STAFF_SEND_STATUS = "staff.send.status";
    
    public static final String QUEUE_APIGW_SEND_LEAD_DOC_CONVERT = "apigw.send.lead.doc.convert";

    public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_CREATION="prepaid.invoice";
    public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_CREATION="postpaid.invoice";
    public static final String QUEUE_POSTPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="postpaid.charge";
    public static final String QUEUE_PREPAID_CUSTOMER_INVOICE_DIRECT_CHARGE="prepaid.charge";
    public static final String QUEUE_CUSTOMER_INVOICE_INVENTORY_CHARGE="inventory.charge";

    public static final String QUEUE_CLIENT_SERVICE_UPDATE="apigw.send.client.service.update";

    public static final String QUEUE_RADIUS_COA_DM = "apiw.send.radius.coadm";

    public static final String QUEUE_RADIUS_CUSTOMER_UPDATE_STATUS = "apiw.send.radius.customer.update.status";

    public static final String QUEUE_WORKFLOW_ACTION_ASSIGN_MESSAGE= "apiw.send.workflow.action.assign.message";

    public static final String WORKFLOW_ASSIGN_ACTION_MESSAGE = "Workflow assign action";

    public static final String WORKFLOW_ASSIGN_ACTION = "Workflow Assign Action";
    
    public static final String QUEUE_APIGW_SEND_MVNO = "apigw.send.mvno";


    public static final String QUEUE_SEND_CUSTOMER_STATUS_CHANGE = "apiw.send.customer.status.change";
    public static final String SEND_CUSTOMER_STATUS_CHANGE_TEMPLATE = "Customer Ticket Status Change";

    public static final String QUEUE_APIGW_SEND_LEAD_MASTER = "apigw.send.leadMaster";

    public static final String QUEUE_APIGW_SERVICE_START_STOP = "apigw.service.status.change";

    public static final String QUEUE_TAT_SEND_PARENT_TO_TEAM = "apigw.tat.send.parent.to.team";

    public static final String TAT_SEND_PARENT_TO_TEAM_FOR_TASK = "TATNOTIFICATIONTOTEAMFORTASK";

    public static final String TAT_NO_RESPONSE_TAKEN = "No response taken by team";

    public static final String QUEUE_PARTNER_INVOICE="partner.invoice";

    public static final String QUEUE_SEND_FOLLOWUP_REMARK_MSG = "apigw.send.followup.remark.msg";
    public static final String SEND_FOLLOWUP_REMARK_MSG = "Followup Remark Message";
    public static  final String FOLLOWUP_REMARK_MSG = "TicketFollowUpRemark";
    public static  final String FOLLOWUP_TASK_MSG = "Task Remark Message";

    public static final String QUEUE_SEND_PROBLEM_DOMAIN_CHANGE_MSG = "send.problem.domain.change.msg";

    public static  final String SEND_PROBLEM_DOMAIN_TEMPLATE_NAME = "TicketDomainChangeMsg";
    public static final String SEND_PROBLEM_DOMAIN_REMARK_MSG = "Problem Domain Change";
    public static final String QUEUE_SEND_NASUPDATE = "bss.radius.send.nasupdate";

    public static final String QUEUE_APIGW_SEND_POP_MANAGEMENT = "apigw.send.popManagement";


    public static final String QUEUE_TICKET_ETR = "bss.ticket.etr";

    public static final String QUEUE_TICKET_ETR_AUDIT = "bss.ticket.etr.audit";

    public static final String QUEUE_CUSTOMER_EMAIL_DOC_AUDIT = "bss.customer.email.etr.audit";
    public static final String  TICKET_ETR_TEMPLATE = "Ticket ETR Template";
    public static final String  TICKET_ETR_TEMPLATE_DYNAMIC = "Ticket ETR Dynamic Template";

    public static final String QUEUE_APIGW_SEND_TEAMS = "apigw.send.teams";


    //template name
    public static final String CAF_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "CAF Follow Up Reminder For Staff";
    public static final String CAF_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "CAF Follow Up Reminder For Customer";
    public static final String CAF_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "CAF Follow Up OverDue For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "CAF Follow Up OverDue For Parent Staff";

  //notification header name
    public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "CAF Follow up Reminder For Customer";
    public static final String CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Reminder For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "CAF Follow up Overdue For Staff";
    public static final String CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "CAF Follow up Overdue For Parent Staff";

    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF = "bss.customer.caf.followup.reminder.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER = "bss.customer.caf.followup.reminder.customer";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF = "bss.customer.caf.followup.overdue.staff";
    public static final String QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.customer.caf.followup.overdue.parent.staff";
    public static final String QUEUE_TEAM_SEND_TASK_MGMT_SUCCESS = "team_create_from_bss_to_task_mgmt";
    public static final String QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS = "business_unit_create_from_bss_to_task_mgmt";
    public static final String QUEUE_BILL_GEN_SEND_INTEGRATION_SYSTEM = "bss.apigw.integrationsytem.billgen";
    public static final String QUEUE_CUSTOMER_SEND_INTEGRATION_SYSTEM = "bss.apigw.integrationsytem.customer";
    public static final String QUEUE_CHARGE_MGMTN_SUCCESS="charge_management";

    public static final String QUEUE_PLAN_SERVICE_SUCCESS="plan_service_management";
    public static final String QUEUE_CUSTOMERS_SUCCESS="customers_management";



//    public static final String QUEUE_SEND_NASUPDATE = "bss.radius.send.nasupdate";


    //ticket followuo template name
    public static final String TICKET_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "TICKET Follow Up Reminder For Staff";
    public static final String TICKET_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "TICKET Follow Up Reminder For Customer";
    public static final String TICKET_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "TICKET Follow Up OverDue For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "TICKET Follow Up OverDue For Parent Staff";

    //ticket followup notification header name
    public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "TICKET Follow up Reminder For Customer";
    public static final String TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Reminder For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "TICKET Follow up Overdue For Staff";
    public static final String TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "TICKET Follow up Overdue For Parent Staff";


    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF = "bss.ticket.followup.reminder.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER = "bss.ticket.followup.reminder.customer";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF = "bss.ticket.followup.overdue.staff";
    public static final String QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.ticket.followup.overdue.parent.staff";


    public static final String QUEUE_TAX_MGMTN_SUCCESS="tax_management";
    public static final String QUEUE_SERVICE_AREA_SUCCESS="service_area";
    public static final String QUEUE_CREDIT_DOCUMENT_SUCCESS="credit_document";
    public static final String QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS="credit_document";

    public static final String QUEUE_BUSINESS_UNIT_SUCCESS="business_unit";
    public static final String QUEUE_DEBIT_DOCUMENT_SUCCESS="debit_document";

    public static final String QUEUE_CUST_PLAN_MAPPING_UPDATE="cpr.debit.update";
    public static final String QUEUE_CANCEL_REGENERATE_SUCCESS="cancel_regenerate";
    public static final String QUEUE_STAFF_MANAGEMENT_SUCCESS="Staff Management";

    public static final String TICKET_CREATION = "Ticket Creation";
    public static final String TASK_CREATION_NOTIFICATION= "Task Creation Notification";

    public static final String QUEUE_TICKET_CREATION_SUCCESS="Staff Management";
    public static final String QUEUE_LEAD_ASSIGN_MESSAGE="lead.assign.message";
    public static final String QUEUE_BRANCH_SUCCESS="branch_success";
    public static final String QUEUE_CUSTOMER_SUCCESS="customer_success";

    public static final String QUEUE_TICKET_RESCHEDULE_SUCCESS_MSG="bss.ticket.reschedule.success.message";

    public static final String TICKET_RESCHEDULE_MESSAGE = "TicketFollowUpRemark";
    public static final String TICKET_RESCHEDULE_SUCCESS_MSG = "Ticket Reschedule Successful";

    public static final String QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN = "bss.apigw.integrationsytem.creditnotegen";

    public static final String TICKET_TAT_REMINDER_NOTIFICATION = "TatBreachedFollowup";
    public static final String TICKET_TAT_REMINDER_NOTIFICATION_MSG = "Tat Breach Reminder";

    public static final String QUEUE_TICKET_TAT_BREACHED_REMINDER = "bss.ticket.tat.breached.reminder";

    public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION = "TatBreachedOverDueFollowup";
    public static final String TICKET_TAT_OVERDUE_REMINDER_NOTIFICATION_MSG = "Tat Breach Overdue Reminder";

    //template name
    public static final String NO_CAF_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE = "No CAF FollowUp Reminder for ParentStaff";
    public static final String NO_CAF_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "No CAF FollowUp Reminder for Staff";

    //message header name
    public static final String NO_LEAD_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_STAFF = "No CAF FollowUp Reminder for Staff";
    public static final String NO_LEAD_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No CAF FollowUp Reminder for ParentStaff";
    public static final String NO_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_STAFF = "No FollowUp Reminder for Staff";
    public static final String NO_FOLLOW_UP_REMINDER_CAF_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No FollowUp Reminder for ParentStaff";

    public static final String NO_FOLLOW_UP_REMINDER_FOR_CAF_STAFF_TEMPLATE = "No FollowUp Reminder for Staff";


    public static final String QUEUE_BSS_NO_FOLLOW_UP_REMINDER_STAFF = "bss.nofollowup.reminder.staff";

    public static final String QUEUE_BSS_NO_FOLLOW_UP_REMINDER_CAF_PARENT_STAFF = "bss.nofollowup.reminder.caf.parent.staff";




    /** RabbitMq for voucher code**/
    public static final String QUEUE_SEND_VOUCHERCODE = "send.vouchercode.queue";

    public static final String CUSTOMER_VOUCHER_TEMPLATE = "Voucher Code";


    /** Rabbitmq for advance notification**/
    public static final String CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE = "Customer Dunning Advance Notification";

    public static final String CUSTOMER_DUNNING_ADVANCE_NOTIFICATION_TEMPLATE_HEADER = "Plan Expiry Reminder";

    public static final String QUEUE_DUNNING_ADVANCE_NOTIFICATION = "send.dunning.advance.notification";

    /** Rabbitmq for partner document**/
    public static final String PARTNER_DUNNING_DOCUMENT_TEMPLATE = "Partner Dunning Document";

    public static final String PARTNER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Partner Document  Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT = "send.dunning.partner.document";

   /**Rabbitmq for partner document ended**/



    /** Rabbitmq for partner document Deactivation started**/
    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE = "Partner Dunning Deactivation Document";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "Partner Document Deactivation Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION = "send.dunning.partner.document.deactivation";

    /**Rabbitmq for partner document deactivation ended**/


    /** Rabbitmq for partner document Deactivation send to staff started**/
    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE = "Staff Document Deactivation";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF_TEMPLATE_HEADER = "Staff Document Deactivation Reminder";

    public static final String QUEUE_PARTNER_DUNNING_DOCUMENT_DEACTIVATION_STAFF = "send.dunning.partner.document.deactivation.staff";

    /**Rabbitmq for partner document deactivation send to staff ended**/



    //    public static final String QUEUE_SEND_SERIAL_NUMBER = "bss.apigateway.send.serialnumber";
    public static final String UPDATE_PLAN_PRICES_IN_CRM = "send.updated.plan.prices";

    //QUEUE_TICKET_OVERDUE_TAT_BREACHED_REMINDER = "bss.ticket.tat.overdue.breached.reminder";
    public static final String QUEUE_COMMON_QUEUE_FOR_ALL_NOTIFICATION = "bss.common.queue.for.all.notification";

    public static final String QUEUE_LEAD_CAF_CONVERTION = "lead.caf.convertion";

    /** for customer inactive status **/
    public static final String CUSTOMER_STATUS_INACTIVATE_TEMPLATE = "Customer Status InActive";
    public static final String CUSTOMER_STATUS_INACTIVATE_EVENT = "Customer Status InActive";
    public static final String QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION = "customer.status.inactive.notification";
    /** for customer inactive status **/


    /** for customer document verification **/
    public static final String CUSTOMER_DOCUMENT_VERIFICATION_TEMPLATE = "Customer Document Verification Template";
    public static final String CUSTOMER_DOCUMENT_VERIFICATION_EVENT = "Customer Document Verification";
    public static final String QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION = "customer.document.verification.notification";
    /** for customer document verification ended**/

    /** for customer service active **/
    public static final String CUSTOMER_SERVICE_ACTIVE_TEMPLATE = "Customer Service Active Template";
    public static final String CUSTOMER_SERVICE_ACTIVE_EVENT = "Customer Service Active";
    public static final String QUEUE_CUSTOMER_SERVICE_ACTIVE_NOTIFICATION = "customer.service.active.notification";
    /** for customer service active ended**/


    /** for customer service inactive **/
    public static final String CUSTOMER_SERVICE_INACTIVE_TEMPLATE = "Customer Service InActive Template";
    public static final String CUSTOMER_SERVICE_INACTIVE_EVENT = "Customer Service InActive";
    public static final String QUEUE_CUSTOMER_SERVICE_INACTIVE_NOTIFICATION = "customer.service.inactive.notification";
    /** for customer service inactive ended**/

    public static final String QUEUE_LEAD_QUOTATION_WF = "send.lead.quotation.wf";
    public static final String QUEUE_SEND_APPROVER_DETAIL_QUOTATION = "send.aprrover.detail.quotation";
    public static final String QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE="lead.quotation.assign.message";

    /** for customer change password**/
    public static final String CUSTOMER_CHANGE_PASSWORD_TEMPLATE = "Customer Change Password Template";
    public static final String CUSTOMER_CHANGE_PASSWORD_EVENT = "Customer Change Password";
    public static final String QUEUE_CUSTOMER_CHANGE_PASSWORD_NOTIFICATION = "customer.change.password.notification";
    /** for customer change password ended**/


    /** Rabbitmq for partner document**/
    public static final String CUSTOMER_DUNNING_DOCUMENT_TEMPLATE = "Customer Dunning Document";

    public static final String CUSTOMER_DUNNING_DOCUMENT_TEMPLATE_HEADER = "Customer Document Reminder";

    public static final String QUEUE_CUSTOMER_DUNNING_DOCUMENT = "send.dunning.customer.document";

    /** for customer open address shifting**/
    public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_TEMPLATE = "Customer Open Address Shifting Template";
    public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT = "Customer Open Address Shifting";
    public static final String QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION = "customer.open.address.shifting.notification";
    /** for customer open address shifting ended**/

    /** for customer close address shifting**/
    public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_TEMPLATE = "Customer Close Address Shifting Template";
    public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT = "Customer Close Address Shifting";
    public static final String QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION = "customer.close.address.shifting.notification";
    /** for customer close address shifting ended**/

    /** for customer payment verification**/
    public static final String CUSTOMER_PAYMENT_VERIFICATION_TEMPLATE = "Customer Payment Verification Template";
    public static final String CUSTOMER_PAYMENT_VERIFICATION_EVENT = "Customer Payment Verification";
    public static final String QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION = "customer.payment.verification.notification";
    /** for customer payment verification ended**/

    /** for ticket close notification**/
    public static final String CUSTOMER_TICKET_CLOSE_TEMPLATE = "Customer Ticket Close Template";
    public static final String CUSTOMER_TICKET_CLOSE_EVENT = "Customer Ticket Close";
    public static final String QUEUE_CUSTOMER_TICKET_CLOSE_NOTIFICATION = "customer.ticket.close.notification";
    /** for customer ticket close ended**/

  /**Rabbitmq added for custpackage rel **/
  public static final String QUEUE_CUSTOMER_PLAN_MAPPING_FOR_INTEGRATION = "apigw.customer.planmapping.integration" ;

    public static final String QUEUE_CUSTOMER_SERVICE_MAPPING_FOR_INTEGRATION = "apigw.customer.servicemapping.integration" ;

    public static final String QUEUE_SERVICE_FOR_INTEGRATION = "apigw.service.integration" ;

    public static final String QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY = "apigw.customer.inventory" ;

    public static final String QUEUE_SERVICE_FOR_INVENTORY_ITEM = "apigw.inventory.item" ;


    public static final String QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION = "apigw.plan.integration";

    public static final String QUEUE_APIGW_CUSTOMER_NOTIFICATION = "apigw.customer.notification.queue";

    public static final String QUEUE_APIGW_LEAD_MILESTONES_MAPPING = "apigw.lead.milestones.mapping";

    public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE = "Customer Dunning Deactivation Document";

    public static final String CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER = "CUSTOMER_DUNNING_DOCUMENT_DEACTIVATION_TEMPLATE_HEADER";


    /** for lead creation notificaton**/
    public static final String LEAD_CREATION_TEMPLATE = "Lead Creation Template";
    public static final String LEAD_CREATION_EVENT = "Lead Creation Success";
    public static final String QUEUE_LEAD_CREATION_NOTIFICATION = "lead.creation.notification";
    /** for lead creation notificaton ended**/


    public static final String QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION = "apigw.approve.item.integration";
    public static final String QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION = "apigw.approve.remove.item.request.integration";
    public static final String QUEUE_APIGW_CREATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.create.custservicechargeipdtls";
    public static final String QUEUE_APIGW_UPDATE_CUST_SERVICE_CHARGE_IP_DTLS = "apigw.update.custservicechargeipdtls";

//    public static final String QUEUE_SEND_SERIAL_NUMBER = "bss.apigateway.send.serialnumber";

    public static final String QUEUE_APIGW_SEND_CUSTOMER_KPI = "apigw.send.customer.kpi";

    public static final String QUEUE_SERVICE_AREA_SUCCESS_KPI="service_area";

    public static final String QUEUE_BUSINESS_UNIT_KPI = "bss.business.unit.kpi";

    public static final String QUEUE_APIGW_BRANCH_KPI = "apigw.branch.kpi";

    public static final String QUEUE_APIGW_CUSTOMER_PACKAGE_REL_KPI = "apigw.customer.package.rel.kpi";

    public static final String QUEUE_PLAN_SERVICE_KPI="plan.service.kpi";

    public static final String QUEUE_DEBIT_DOCUMENT_SUCCESS_KPI="debit.document.kpi";

    public static final String QUEUE_CREDIT_DOCUMENT_KPI="credit.document.kpi";

    public static final String QUEUE_CUSTOMER_SERVICE_MAPPING_KPI = "apigw.customer.servicemapping.kpi" ;
    public static final String QUEUE_APIGW_TICKET_MESSAGE_INTEGRATION_SYSTEM = "apigw.ticketmessage.integrationsytem";

    public static final String QUEUE_REQUEST_GATEWAY_FOR_STAFFUSER = "request.apigw.get.staffuser";

    public static final String QUEUE_RESPONSE_GATEWAY_FOR_STAFFUSER = "response.apigw.get.staffuser";

    public static final String QUEUE_RESPONSE_TO_SAVE_STAFFUSER_FROM_GATEWAY = "response.apigw.to.kpi.save.staffuser";
    public static final String QUEUE_TICKET_TAT_AUDIT = "bss.ticket.tat.audit";

    public static final String QUEUE_TICKET_TAT_SUCCESS_MESSAGE= "bss.ticket.tat.success.message";

    public static final String QUEUE_COUNTRY = "country.queue";
    public static final String QUEUE_STATE = "state.queue";
    public static final String QUEUE_CITY = "city.queue";

    public static final String QUEUE_PLANGROUP_SALESCRM = "queue.plangroup.salescrm";
    public static final String QUEUE_PINCODE = "pincode.queue";
    public static final String QUEUE_AREA = "area.queue";
    public static final String QUEUE_INTEGRATION_CREATE_SELFCARE_TICKET = "integration.create.selfcareticket";
    /** for staff sending message **/
    public static final String QUEUE_STAFF_USER_SEND = "apigw.staff.user.queue";
    public static final String QUEUE_STAFF_SAVE_USER_SEND = "apigw.staff.save.user.queue";
    public static final String QUEUE_STAFFUSER_SEND_DELETE = "apigw.staff.user.queue.delete";
    public static final String QUEUE_PRODUCT_FROM_RMS = "apigw.product.from.rms.integration";
    public static final String QUEUE_PRODUCTCATEGORY_INTEGRATOIN = "apigw.productcategory.integration";

    public static final String QUEUE_WAREHOUSE_INTEGRATOIN = "apigw.warehouse.integration";
    public static final String QUEUE_INWARD_RMS_INTEGRATOIN = "apigw.inward.rms.integration";
    public static final String QUEUE_SERIALIZED_ITEM_FROM_RMS_INTEGRATOIN = "apigw.serialized.item.from.rms.integration";
    public static final String QUEUE_SERIALIZED_ITEM_HISTORY_RMS_INTEGRATOIN = "apigw.serialized.item.history.rms.integration";
    public static final String QUEUE_SEND_INWARD_TO_INTEGRATOIN = "apigw.send.inward.to.integration";

    public static final String QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET = "queue.country.create.data.share.ticket";
    public static final String QUEUE_STATE_CREATE_DATA_SHARE_TICKET = "queue.state.create.data.share.ticket";
    public static final String QUEUE_CITY_CREATE_DATA_SHARE_TICKET = "queue.city.create.data.share.ticket";



    public static final String QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET = "queue.country.update.data.share.ticket";
    public static final String QUEUE_STATE_UPDATE_DATA_SHARE_TICKET = "queue.state.update.data.share.ticket";
    public static final String QUEUE_CITY_UPDATE_DATA_SHARE_TICKET = "queue.city.update.data.share.ticket";


    public static final String QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET = "queue.pincode.create.data.share.ticket";

    public static final String QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET = "queue.pincode.update.data.share.ticket";

    public static final String QUEUE_AREA_CREATE_DATA_SHARE_TICKET = "queuea.area.create.data.share.ticket";
    public static final String QUEUE_AREA_UPDATE_DATA_SHARE_TICKET = "queue.area.update.data.share.ticket";

    public static final String QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET = "queuea.service.area.create.data.share.ticket";
    public static final String QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET = "queue.service.area.update.data.share.ticket";

    public static final String QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET = "queue.businessunit.create.data.share.ticket";
    public static final String QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET = "queue.businessunit.update.data.share.ticket";
    public static final String QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET = "queue.branch.create.data.share.ticket";
    public static final String QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET = "queue.branch.update.data.share.ticket";


    public static final String QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET = "queue.teams.create.data.share.ticket";
    public static final String QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET = "queue.teams.update.data.share.ticket";

    public static final String QUEUE_HIERARCHY_CREATE_DATA_SHARE_TICKET = "queue.hierarchy.create.data.share.ticket";
    public static final String QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET = "queue.hierarchy.update.data.share.ticket";

    public static final String QUEUE_STAFF_CREATE_DATA_SHARE_TICKET = "queue.staff.create.data.share.ticket";
    public static final String QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET = "queue.staff.update.data.share.ticket";


    public static final String QUEUE_REGION_CREATE_DATA_SHARE_TICKET = "queue.region.create.data.share.ticket";
    public static final String QUEUE_REGION_UPDATE_DATA_SHARE_TICKET = "queue.region.update.data.share.ticket";


    public static final String QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET = "queue.busverticals.create.data.share.ticket";
    public static final String QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET = "queue.busverticals.update.data.share.ticket";

    public static final String QUEUE_CUSTOMERS_CREATE_DATA_SHARE_TICKET = "queue.customers.create.data.share.ticket";
    public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET = "queue.customers.update.data.share.ticket";

    public static final String QUEUE_ROLE_CREATE_DATA_SHARE_TICKET = "queue.role.create.data.share.ticket";
    public static final String QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET = "queue.role.update.data.share.ticket";

    public static final String QUEUE_MVNO_CREATE_DATA_SHARE_TICKET = "queue.mvno.create.data.share.ticket";
    public static final String QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET = "queue.mvno.update.data.share.ticket";

    public static final String QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET = "queue.services.create.data.share.ticket";
    public static final String QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET = "queue.services.update.data.share.ticket";


    public static final String QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET = "queue.partner.create.data.share.ticket";
    public static final String QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET = "queue.partner.update.data.share.ticket";
    public static final String QUEUE_PLAN_CREATE_DATA_SHARE_TICKET = "queue.plan.create.data.share.ticket";
    public static final String QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET = "queue.plan.update.data.share.ticket";

    public static final String QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE = "queue.client.serv.save.data.share.ticket.microservice";
    public static final String QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE = "queue.client.serv.update.data.share.ticket.microservice";
    public static final String QUEUE_SEND_TICKET_DATA_TO_APIGW = "queue.send.ticket.data.to.apigw";
    public static final String QUEUE_SEND_UPDATED_TICKET_DATA_TO_APIGW = "queue.send.updated.ticket.data.to.apigw";
    public static  final String TASK_RESCHEDUKE_NOTIFICATION="task reaschedule";

    public static final String TATNOTIFICATION = "TATNOTIFICATION";
}
