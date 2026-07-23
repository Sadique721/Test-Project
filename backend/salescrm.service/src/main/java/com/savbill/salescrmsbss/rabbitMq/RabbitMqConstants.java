package com.savbill.salescrmsbss.rabbitMq;

public class RabbitMqConstants {
	public static final String SAVBILL_EXCHANGE = "savbill.exchange";

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
	public static final String TEAM_NAME = "TeamName";

	public static final String SLICE_CHUNK = "slicechunk";

	public static final String QUEUE_COUNTRY = "country.queue";
	public static final String QUEUE_STATE = "state.queue";
	public static final String QUEUE_CITY = "city.queue";
	public static final String QUEUE_PINCODE = "pincode.queue";
	public static final String QUEUE_AREA = "area.queue";
	public static final String QUEUE_SERVICE_AREA = "service.area.queue";
	public static final String QUEUE_PARTNER = "partner.queue";   // This queue is not received from anywhere
	public static final String QUEUE_CLIENT_SERVICE = "client.service.queue";  // This queue is not received from anywhere
	public static final String QUEUE_PLAN_GROUP = "plan.group.queue";  //This queue is not received from anywhere
	public static final String QUEUE_NETWORK_DEVICES = "network.devices.queue";

	// user create
	public static final String ROLE = "bss.role";
	public static final String QUEUE_USER = "bss.user";

	// businessunit create
	public static final String QUEUE_BUSINESS_UNIT = "bss.business.unit";

	public static final String CREATE_FOLLOW_UP_TEMPLATE_HEADER = "Create FollowUp";
	public static final String SOURCE_NAME_SALES_CRMS_BSS = "Sales Crms BSS API";

	public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF = "bss.lead.followup.reminder.staff";
	public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER = "bss.lead.followup.reminder.customer";
	public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF = "bss.lead.followup.overdue.staff";
	public static final String QUEUE_SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "bss.lead.followup.overdue.parent.staff";
	public static final String QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF = "bss.lead.nofollowup.reminder.staff";
	public static final String QUEUE_SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF = "bss.lead.nofollowup.reminder.parent.staff";
	public static final String QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF = "bss.nofollowup.reminder.staff";
	public static final String QUEUE_SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF = "bss.nofollowup.reminder.parent.staff";

	// template name
	public static final String FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "Follow Up Reminder For Staff";
	public static final String FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE = "Follow Up Reminder For Customer";
	public static final String FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE = "Follow Up OverDue For Staff";
	public static final String FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE = "Follow Up OverDue For Parent Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "No Lead FollowUp Reminder for Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE = "No Lead FollowUp Reminder for ParentStaff";
	public static final String NO_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE = "No FollowUp Reminder for Staff";
	public static final String NO_FOLLOW_UP_REMINDER_FOR_PARENT_STAFF_TEMPLATE = "No FollowUp Reminder for ParentStaff";

	// notification header name
	public static final String FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER = "Follow up Reminder For Customer";
	public static final String FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "Follow up Reminder For Staff";
	public static final String FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF = "Follow up Overdue For Staff";
	public static final String FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF = "Follow up Overdue For Parent Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "No Lead FollowUp Reminder for Staff";
	public static final String NO_LEAD_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No Lead FollowUp Reminder for ParentStaff";
	public static final String NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF = "No FollowUp Reminder for Staff";
	public static final String NO_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_PARENT_STAFF = "No FollowUp Reminder for ParentStaff";

	// Lead Mgmt workfloq queues

	public static final String QUEUE_FIND_STAFF_FOR_LEAD = "bss.staff.find.for.lead";

	public static final String QUEUE_COUNT_FOR_STAFF = "bss.count.for.staff";

	public static final String QUEUE_LEAD_MGMT_INIT_DATA = "bss.lead.mgmt.init.data";
	public static final String QUEUE_SEND_APPROVER_DETAIL = "send.aprover.detail";
	public static final String QUEUE_SEND_APPROVER_UPDATE_DETAIL = "send.aprover.update.detail";
	public static final String QUEUE_SEND_UPDATE_LEAD_INFO = "send.updated.lead.info";
	public static final String QUEUE_SEND_LEAD_STATUS_INFO = "send.lead.status.info";  //not in use

	public static final String QUEUE_SEND_LEAD_STATUS_DTO = "send.lead.status.dto";//not in use
	public static final String QUEUE_SEND_CONVERT_CUSTOMER_CAF_POJO = "send.convert.customer.caf.salescrms.pojo";
	public static final String QUEUE_APIGW_SEND_BRANCH = "apigw.send.branch";

	public static final String QUEUE_APIGW_SEND_PARTNER = "apigw.send.partner";

	public static final String QUEUE_APIGW_SEND_SERVICE_AREA = "apigw.send.servicearea";

	public static final String QUEUE_APIGW_SEND_CUSTOMER = "apigw.send.customer";

	public static final String QUEUE_APIGW_CUSTOMER_STATUS_UPDATE = "apigw.customer.status.queue";

	public static final String QUEUE_APIGW_SEND_LEAD_DOC_CONVERT = "apigw.send.lead.doc.convert";

	public static final String QUEUE_CLIENT_SERVICE_UPDATE = "apigw.send.client.service.update";

	public static final String QUEUE_APIGW_SEND_MVNO = "apigw.send.mvno";

	public static final String QUEUE_APIGW_SEND_LEAD_MASTER = "apigw.send.leadMaster";

	public static final String QUEUE_APIGW_SEND_POP_MANAGEMENT = "apigw.send.popManagement";

	public static final String QUEUE_APIGW_SEND_TEAMS = "apigw.send.teams";

	public static final String QUEUE_LEAD_ASSIGN_MESSAGE = "lead.assign.message";
	public static final String QUEUE_LEAD_CAF_CONVERTION = "lead.caf.convertion";

	public static final String UPDATE_PLAN_PRICES_IN_CRM = "send.updated.plan.prices";

	public static final String EMAIL_NOTIFICATION_FOR_CUSTOMER_WITH_LEADQUOTATION = "Email notification for customer with lead quotation";

    public static final String QUEUE_LEAD_QUOTATION_WF = "send.lead.quotation.wf";
    public static final String QUEUE_SEND_APPROVER_DETAIL_QUOTATION = "send.aprrover.detail.quotation";
    public static final String QUEUE_LEAD_QUOTATION_ASSIGN_MESSAGE="lead.quotation.assign.message";

	public static final String QUEUE_APIGW_LEAD_MILESTONES_MAPPING = "apigw.lead.milestones.mapping";

	public static final String QUEUE_PLANGROUP_SALESCRM = "queue.plangroup.salescrm";
	public static final String QUEUE_PLANGROUP_SALESCRM_UPDATE = "queue.plangroup.salescrm.update";
	public static final String QUEUE_SEND_CREATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM = "queue.create.systemconfiguration.common.apigw.to.salescrm";
	public static final String QUEUE_SEND_UPDATE_SYSTEM_CONFIGURATION_COMMON_APIGW_TO_SALESCRM = "queue.update.systemconfiguration.common.apigw.to.salescrm";

	public static final String QUEUE_PARTNER_UPDATE_DATA_SHARE_SALESCRM = "queue.partner.update.data.share.salescrm";
	public static final String QUEUE_PARTNER_CREATE_DATA_SHARE_SALESCRM = "queue.partner.create.data.share.salescrm";


	public static final String QUEUE_SEND_CREATE_DATA_ROLE_CRM = "queue.send.create.data.role.to.crm";
	public static final String QUEUE_SEND_DELETE_DATA_ROLE_CRM = "queue.send.delete.data.role.to.crm";
	public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET = "queue.customers.update.data.share.ticket";
	public static final String QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM = "queue.customers.update.data.share.salescrm";

	public static final String QUEUE_SEND_UPDATE_MVNO_COMMON_APIGW_TO_SALES_CRM_ISP = "queue.create.mvno.common.apigw.to.salescrm.isp";

}
