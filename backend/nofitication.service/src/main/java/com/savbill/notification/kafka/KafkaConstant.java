package com.savbill.notification.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static final String KAFKA_COMMON_TOPIC = "common";

    public static final String KAFKA_CMS_TOPIC = "cms";

    public static final String KAFKA_PMS_TOPIC = "pms";

    public static final String KAFKA_INVENTORY_TOPIC = "inventory";

    public static final String KAFKA_REVENUE_TOPIC = "revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC = "notification";

    public static final String KAFKA_RADIUS_TOPIC = "radius";

    public static final String KAFKA_TICKET_TOPIC = "ticket";

    public static final String KAFKA_SALES_CRM_TOPIC = "sales";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA = "SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

    public static final String KAFKA_TASK_TOPIC = "task";

    public static final String OPT_FOR_PORTAL = "OPT_FOR_PORTAL";

    public static final String OPT_FOR_LOGIN_2FA = "OPT_FOR_LOGIN_2FA";

    public static final String TICKET_ASSIGN_TEAM_SUCCESS = "TICKET_ASSIGN_TEAM_SUCCESS";
    public static final String TASK_ASSIGN_TEAM_SUCCESS = "TASK_ASSIGN_TEAM_SUCCESS";

    public static final String BSS_CUSTOMER_DUNNING = "BSS_CUSTOMER_DUNNING";


    //KAFKA-GROUP-ID'S

    public static final String KAFKA_NOTIFICATION_GROUP = "notification-group";

    public static final String KAFKA_CMS_GROUP_ID = "notification-cms-group";

    public static final String KAFKA_TASK_GROUP_ID = "task-common-group";

    public static final String KAFKA_PMS_GROUP_ID = "notification-pms-group";

    public static final String KAFKA_INVENTORY_GROUP_ID = "notification-inventory-group";

    public static final String KAFKA_COMMON_GROUP_ID = "notification-common-group";

    public static final String KAFKA_REVENUE_GROUP_ID = "notification-revenue-group";

    public static final String KAFKA_RADIUS_GROUP_ID = "notification-radius-group";

    public static final String KAFKA_TICKET_GROUP_ID = "notification-ticket-group";

    public static final String SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF = "SALES_CRMS_BSS_FOLLOW_UP_REMINDER_STAFF";

    public static final String SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER = "SALES_CRMS_BSS_FOLLOW_UP_REMINDER_CUSTOMER";

    public static final String SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF = "SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_STAFF";
    public static final String SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "SALES_CRMS_BSS_FOLLOW_UP_OVER_DUE_PARENT_STAFF";

    public static final String SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF = "SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_STAFF";

    public static final String SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF = "SALES_CRMS_BSS_NO_LEAD_FOLLOW_UP_REMINDER_PARENT_STAFF";

    public static final String SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF = "SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_STAFF";

    public static final String SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF = "SALES_CRMS_BSS_NO_FOLLOW_UP_REMINDER_PARENT_STAFF";

    public static final String QUEUE_SEND_FOLLOWUP_REMARK_MSG = "QUEUE_SEND_FOLLOWUP_REMARK_MSG";

    public static final String SEND_PROBLEM_DOMAIN_CHANGE_MSG = "SEND_PROBLEM_DOMAIN_CHANGE_MSG";

    public static final String SEND_TASK_CATEGORY_CHANGE_MSG = "SEND_TASK_CATEGORY_CHANGE_MSG";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF = "SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER = "SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF = "SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF";
    public static final String TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF = "TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF";
    public static final String TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER = "TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER";

    public static final String TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF = "TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF";

    public static final String TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF = "TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF";

    public static final String DUNNING_ADVANCE_NOTIFICATION = "DUNNING_ADVANCE_NOTIFICATION";

    public static final String PARTNER_DUNNING_DOCUMENT = "PARTNER_DUNNING_DOCUMENT";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION = "PARTNER_DUNNING_DOCUMENT_DEACTIVATION";

    public static final String CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION = "CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION";

    public static final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION = "CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION";

    public static final String TICKET_TAT_SUCCESS_MESSAGE = "TICKET_TAT_SUCCESS_MESSAGE";

    public static final String TASK_TAT_SUCCESS_MESSAGE = "TASK_TAT_SUCCESS_MESSAGE";

    public static final String SEND_QUOTA_NOTIFICATION_CUSTOMER = "SEND_QUOTA_NOTIFICATION_CUSTOMER";

    public static final String SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER = "SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER";

    public static final String CAF_TAT_SUCCESS_MESSAGE = "CAF_TAT_SUCCESS_MESSAGE";

    public static final String TREMINATION_TAT_SUCCESS_MESSAGE = "TREMINATION_TAT_SUCCESS_MESSAGE";

    public static final String LEAD_TAT_SUCCESS_MESSAGE = "LEAD_TAT_SUCCESS_MESSAGE";

    public static final String SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION = "SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION";

    public static final String SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION = "SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION";

    public static final String SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION = "SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION";
    public static final String SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION = "SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION";

    public static final String CUSTOMER_DOCUMENT_DUNNING_TO_STAFF = "CUSTOMER_DOCUMENT_DUNNING_TO_STAFF";

    public static final String IPS_TO_ISP = "IPS_TO_ISP";

    public static final String TAT_SEND_PARENT_TO_TEAM = "TATNOTIFICATIONTOTEAM";
    public static final String TAT_SEND_PARENT_TO_TEAM_FOR_TASK = "TATNOTIFICATIONTOTEAMFORTASK";

    public static final String TICKET_TAT_AUDIT_SUCCESS = "TICKET_TAT_AUDIT_SUCCESS";

    public static final String TICKET_ETR_AUDIT_SUCCESS = "TICKET_ETR_AUDIT_SUCCESS";

    public static final String TICKET_TAT_AUDIT_FAIL = "TICKET_TAT_AUDIT_FAIL";

    public static final String TICKET_ETR_AUDIT_FAIL = "TASK_ETR_AUDIT_FAIL";

    public static final String TASK_ETR_AUDIT_SUCCESS = "TASK_ETR_AUDIT_SUCCESS";
    public static final String TASK_TAT_AUDIT_SUCCESS = "TASK_TAT_AUDIT_SUCCESS";
    public static final String TASK_ETR_AUDIT_FAIL = "TASK_ETR_AUDIT_FAIL";
    public static final String TASK_TAT_AUDIT_FAIL = "TASK_TAT_AUDIT_FAIL";

    public static final String SEND_EXTERNAL_REMARK_FOR_TICKET = "SEND_EXTERNAL_REMARK_FOR_TICKET";
    public static final String SEND_EXTERNAL_REMARK_FOR_TASK = "SEND_EXTERNAL_REMARK_FOR_TASK";

    public static final String SEND_TICKET_ALERT_REMARK = "SEND_TICKET_ALERT_REMARK";

    public static final String SEND_TASK_ALERT_REMARK = "SEND_TASK_ALERT_REMARK";
    public  static  final String TASK_ETR_MSG="TaskETRMsg";
    public static final String FOLLOWUP_TASK_MSG= "Task Followup Remark";
    public static  final String TASK_RESCHEDUKE_NOTIFICATION="task reaschedule";
    public static final String TASK_FOR_STAFF = "Staff";
    public static final String TASK_FOR_CUSTOMER = "Customer";
    public static final String TATNOTIFICATION ="TATNOTIFICATION";
    public static final String TASK_CREATION_NOTIFICATION ="TASK_CREATION_NOTIFICATION";



}
